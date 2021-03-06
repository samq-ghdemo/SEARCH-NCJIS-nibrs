/*
 * Copyright 2016 SEARCH-The National Consortium for Justice Information and Statistics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.search.nibrs.web.uploadfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.common.NIBRSJsonError;
import org.search.nibrs.importer.ReportListener;
import org.search.nibrs.model.AbstractReport;
import org.search.nibrs.util.NibrsFileUtils;
import org.search.nibrs.validate.common.SubmissionFileValidator;
import org.search.nibrs.validation.SubmissionValidator;
import org.search.nibrs.web.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes({"showUserInfoDropdown"})
public class UploadFileController {
	private final Log log = LogFactory.getLog(this.getClass());
	@Resource
	AppProperties appProperties;

	@Autowired
	SubmissionValidator submissionValidator;

	@Autowired
	SubmissionFileValidator submissionFileValidator;
	
	final List<String> acceptedFileTypes = 
			Arrays.asList("application/zip", "text/plain", "application/octet-stream", "text/xml", "application/xml", "application/x-zip-compressed");
	
	@GetMapping("/")
	public String getFileUploadForm(Model model) throws IOException {
	
	    return "index";
	}
	
	@GetMapping("/home")
	public String getFrontPage(Model model) throws IOException {
		
		return "home";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    return "redirect:" + appProperties.getSignOutUrl();
	}

	@GetMapping("/logoutSuccess")
	public String logoutSuccess(Model model) throws IOException {
		
		return "logoutSuccess";
	}
	
    @PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile[] multipartFiles,
			RedirectAttributes redirectAttributes, Model model) throws IOException, ParserConfigurationException {

		log.info("processing file: " + multipartFiles.length);
		
		List<NIBRSError> filteredErrorList = getNibrsErrors(multipartFiles); 
		model.addAttribute("errorList", filteredErrorList);
        return "validationReport :: #content";
    }

    @PostMapping("/json")
    public @ResponseBody List<NIBRSJsonError> getNibrsErrorInJson(@RequestParam("file") MultipartFile[] multipartFiles,
    		RedirectAttributes redirectAttributes, Model model) throws IOException, ParserConfigurationException {
    	
    	log.info("processing file: " + multipartFiles.length);
    	
    	List<NIBRSError> filteredErrorList = getNibrsErrors(multipartFiles); 
    	
    	//Translate NIBRSError to a simplified JSON error class so it can be more easily consumed 
    	
    	//Create array here
    	List<NIBRSJsonError> jsonErrors = new ArrayList<NIBRSJsonError>();
    	
    	for (NIBRSError nibrsError : filteredErrorList)
    	{
    		NIBRSJsonError nibrsJsonError = createNibrsJsonError(nibrsError);
    		
    		jsonErrors.add(nibrsJsonError);
    	}	
    	
    	return jsonErrors;
    }

	private NIBRSJsonError createNibrsJsonError(NIBRSError nibrsError) {
		NIBRSJsonError nibrsJsonError = new NIBRSJsonError();
		
		nibrsJsonError.setSubmissionDate(nibrsError.getDateOfTape());
		
		nibrsJsonError.setSourceLocation(nibrsError.getContext().getSourceLocation());
		
		nibrsJsonError.setActionType(nibrsError.getReport().getReportActionType());
		
		nibrsJsonError.setOri(nibrsError.getReport().getOri());
		
		nibrsJsonError.setIncidentNumber(nibrsError.getReportUniqueIdentifier());
		
		nibrsJsonError.setSegment(nibrsError.getSegmentTypeOutput());
		
		nibrsJsonError.setWithinSegmentIdentifier(nibrsError.getOffenseSegmentIdentifier());
		
		nibrsJsonError.setWithinOffenderArrestVictim(nibrsError.getOffenderArresteeVictimSegmentIdentifier());
		
		nibrsJsonError.setWithinProperty(nibrsError.getPropertySegmentIdentifier());
		
		nibrsJsonError.setDataElement(nibrsError.getDataElementIdentifierOutput());
		
		nibrsJsonError.setErrorCode(nibrsError.getNIBRSErrorCode().getCode());
		
		//Add if statement here
		if (StringUtils.isNotBlank(nibrsError.getRuleNumber()))
		{
			if (nibrsError.getRuleNumber().equals("404") || nibrsError.getRuleNumber().equals("35") || nibrsError.getRuleNumber().equals("342"))
			{
				if (nibrsError.getValue() != null)
				{	
					nibrsJsonError.setRejectedValue(nibrsError.getValue().toString());
				}
			}
			else
			{
				nibrsJsonError.setRejectedValue(nibrsError.getOffendingValues());
			}	
		}
			
		nibrsJsonError.setErrorMessage(nibrsError.getErrorMessage());
		return nibrsJsonError;
	}
    
	private List<NIBRSError> getNibrsErrors(MultipartFile[] multipartFiles)
			throws IOException, ParserConfigurationException {
		final List<NIBRSError> errorList = new ArrayList<>();
		ReportListener validatorListener = new ReportListener() {
			@Override
			public void newReport(AbstractReport report, List<NIBRSError> el) {
				errorList.addAll(el);
				errorList.addAll(submissionValidator.validateReport(report));
			}
		};
		
		for (MultipartFile multipartFile: multipartFiles){
			if (!acceptedFileTypes.contains(multipartFile.getContentType())){
				throw new IllegalArgumentException("The file type is not supported"); 
			}
			
			if (multipartFile.getContentType().equals("application/zip") || multipartFile.getContentType().equals("application/x-zip-compressed")){
				validateZippedFile( validatorListener, multipartFile.getInputStream());
			}
			else {
				submissionFileValidator.validateInputStream(
						validatorListener, multipartFile.getContentType(), multipartFile.getInputStream(), "console");
			}
			
		}
		
		List<NIBRSError> filteredErrorList = errorList.stream()
				.filter(error->error.getReport() != null)
				.collect(Collectors.toList());
		return filteredErrorList;
	}

	@GetMapping("/about")
	public String getAbout(Model model){
	
	    return "about";
	}
	
	@GetMapping("/toolLimitations")
	public String getToolLimitations(Model model){
		
		return "toolLimitations";
	}
	
	@GetMapping("/resources")
	public String getResources(Model model){
		
		return "resources";
	}
	
	@GetMapping("/testFiles")
	public String getTestFiles(Model model){
		
		return "testFiles";
	}
	

	/**
	 * Get only the first entry of the zipped resource. 
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	private void validateZippedFile(ReportListener validatorlistener, InputStream inputStream) throws IOException {
		ZipInputStream zippedStream = new ZipInputStream(inputStream);

		ZipEntry zipEntry = zippedStream.getNextEntry();
		while ( zipEntry != null)
		{
			log.info("Unzipping " + zipEntry.getName());
	        ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        for (int c = zippedStream.read(); c != -1; c = zippedStream.read()) {
	          bout.write(c);
	        }
	        
	        zippedStream.closeEntry();
			
			ByteArrayInputStream inStream = new ByteArrayInputStream( bout.toByteArray() );
			bout.close();
			
		    String mediaType = NibrsFileUtils.getMediaType(inStream);

		    try {
		    	submissionFileValidator.validateInputStream(validatorlistener, mediaType, inStream, "console");
			} catch (ParserConfigurationException e) {
				log.error("Got exception while parsing the file " + zipEntry.getName(), e);
			}
			zipEntry = zippedStream.getNextEntry();
		}
		zippedStream.close();
        inputStream.close();
	}
	
}


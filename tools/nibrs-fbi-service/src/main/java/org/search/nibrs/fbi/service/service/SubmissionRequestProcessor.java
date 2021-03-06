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
package org.search.nibrs.fbi.service.service;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.search.nibrs.xml.XmlUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
@Scope("prototype")
public class SubmissionRequestProcessor {
	private final Log log = LogFactory.getLog(SubmissionRequestProcessor.class);

	public Document processSubmissionRequest(@Body Document nibrsSubmission, Exchange exchange) throws Exception{

		ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate(); 
		log.debug(XmlUtils.nodeToString(nibrsSubmission));
		log.debug("In processSubmissionRequest()");
		Exchange returnedExchange = producerTemplate.send("direct:submitNiemDocument", exchange);
		Document returnedResponse = returnedExchange.getIn().getBody(Document.class);
		log.debug(XmlUtils.nodeToString(returnedResponse));
		return returnedResponse; 
	}
	
	
}
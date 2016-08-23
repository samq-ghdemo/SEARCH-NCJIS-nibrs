package org.search.nibrs.validation.groupa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.model.GroupAIncidentReport;
import org.search.nibrs.model.codes.NIBRSErrorCode;
import org.search.nibrs.validation.RuleViolationExemplarFactory;

public class GroupAIncidentReportValidatorTest {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(GroupAIncidentReportValidatorTest.class);
		
	private GroupAIncidentReportValidator validator;
	private RuleViolationExemplarFactory exemplarFactory;
	
	@Before
	public void init() {
		validator = new GroupAIncidentReportValidator();
		exemplarFactory = RuleViolationExemplarFactory.getInstance();
	}
	
	@Test
	public void testRule101() {
		testRule(NIBRSErrorCode._101, 101);
	}
		
	@Test
	public void testRule104() {
		testRule(NIBRSErrorCode._104, 104);
	}

	@Test
	public void testRule115() {
		testRule(NIBRSErrorCode._115, 115);
		// note that rule 116 is a duplicate of 115 (essentially) so we implement them both with 115
		testRule(NIBRSErrorCode._115, 116);
	}
		
	@Test
	public void testRule117() {
		testRule(NIBRSErrorCode._117, 117);
	}

	@Test
	public void testRule119() {
		testRule(NIBRSErrorCode._119, 119);
	}

	@Test
	public void testRule152() {
		testRule(NIBRSErrorCode._152, 152);
	}
	
	@Test
	public void testRule153() {
		testRule(NIBRSErrorCode._153, 153);
	}
	
	@Test
	public void testRule155() {
		testRule(NIBRSErrorCode._155, 155);
	}
	
	@Test
	public void testRule156() {
		testRule(NIBRSErrorCode._156, 156);
	}
	
	@Test
	public void testRule170() {
		testRule(NIBRSErrorCode._170, 170);
	}
	
	@Test
	public void testRule171() {
		testRule(NIBRSErrorCode._171, 171);
	}
	
	@Test
	public void testRule172() {
		testRule(NIBRSErrorCode._172, 172);
	}
	
	private void testRule(NIBRSErrorCode ruleCode, int ruleNumber) {
		List<GroupAIncidentReport> exemplars = exemplarFactory.getGroupAIncidentsThatViolateRule(ruleNumber);
		for (GroupAIncidentReport r : exemplars) {
			List<NIBRSError> errorList = validator.validate(r);
			boolean found = false;
			for (NIBRSError e : errorList) {
				if (ruleCode == e.getNIBRSErrorCode()) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}
		
}

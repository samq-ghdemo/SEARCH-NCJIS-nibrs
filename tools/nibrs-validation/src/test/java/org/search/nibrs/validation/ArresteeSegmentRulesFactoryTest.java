/*******************************************************************************
 * Copyright 2016 Research Triangle Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.search.nibrs.validation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.common.ParsedObject;
import org.search.nibrs.common.ReportSource;
import org.search.nibrs.model.ArresteeSegment;
import org.search.nibrs.model.GroupAIncidentReport;
import org.search.nibrs.model.GroupBArrestReport;
import org.search.nibrs.model.codes.ArresteeWasArmedWithCode;
import org.search.nibrs.model.codes.AutomaticWeaponIndicatorCode;
import org.search.nibrs.model.codes.ClearedExceptionallyCode;
import org.search.nibrs.model.codes.DispositionOfArresteeUnder18Code;
import org.search.nibrs.model.codes.MultipleArresteeSegmentsIndicator;
import org.search.nibrs.model.codes.NIBRSErrorCode;
import org.search.nibrs.model.codes.OffenseCode;
import org.search.nibrs.model.codes.SexCode;
import org.search.nibrs.model.codes.TypeOfArrestCode;
import org.search.nibrs.validation.rules.Rule;

public class ArresteeSegmentRulesFactoryTest {
	
	private ArresteeSegmentRulesFactory groupARulesFactory = ArresteeSegmentRulesFactory.instance(ArresteeSegmentRulesFactory.GROUP_A_ARRESTEE_MODE);
	private ArresteeSegmentRulesFactory groupBRulesFactory = ArresteeSegmentRulesFactory.instance(ArresteeSegmentRulesFactory.GROUP_B_ARRESTEE_MODE);
	
	@Test
	public void testRule71() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule71();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		GroupAIncidentReport parent = (GroupAIncidentReport) arresteeSegment.getParentReport();
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.JANUARY, 1);
		LocalDate d = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		arresteeSegment.setArrestDate(Date.from(d.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		c.set(2015, Calendar.DECEMBER, 31);
		parent.setExceptionalClearanceDate(c.getTime());
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		c.set(2016, Calendar.DECEMBER, 31);
		parent.setExceptionalClearanceDate(c.getTime());
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		parent.setExceptionalClearanceCode(ClearedExceptionallyCode.N.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		parent.setExceptionalClearanceCode(ClearedExceptionallyCode.A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._071, nibrsError.getNIBRSErrorCode());
		assertEquals("42", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getArrestDate(), nibrsError.getValue());
		assertEquals(parent, nibrsError.getReport());
		c.set(2015, Calendar.DECEMBER, 31);
		parent.setExceptionalClearanceDate(c.getTime());
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRule601ForSequenceNumber() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForSequenceNumber();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArresteeSequenceNumber(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("40", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setArresteeSequenceNumber(0);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(0, nibrsError.getValue());
		arresteeSegment.setArresteeSequenceNumber(100);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		arresteeSegment.setArresteeSequenceNumber(1);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRule701ForSequenceNumber() {
		Rule<ArresteeSegment> rule = groupBRulesFactory.getRuleX01ForSequenceNumber();
		ArresteeSegment arresteeSegment = buildBaseGroupBSegment();
		arresteeSegment.setArresteeSequenceNumber(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._701, nibrsError.getNIBRSErrorCode());
		assertEquals(arresteeSegment.getParentReport(), nibrsError.getReport());
		// don't need to test the rest...it's tested in the Group A version
	}
	
	@Test
	public void testRule601ForArrestTransactionNumber() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForArrestTransactionNumber();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArrestTransactionNumber(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("41", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setArrestTransactionNumber("AB123456789");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRule601ForArrestDate() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForArrestDate();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArrestDate(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("42", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.JANUARY, 1);
		arresteeSegment.setArrestDate(c.getTime());
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRuleX05() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX05();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArrestDate(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		LocalDate arrestDate = LocalDate.of(2016, 2, 15);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		GroupAIncidentReport incident = (GroupAIncidentReport) arresteeSegment.getParentReport();
		incident.setYearOfTape(2016);
		incident.setMonthOfTape(2);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		incident.setMonthOfTape(1);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._605, nibrsError.getNIBRSErrorCode());
		assertEquals("42", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getArrestDate(), nibrsError.getValue());
		arrestDate = LocalDate.of(2016, 1, 31);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arrestDate = LocalDate.of(2016, 1, 1);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arrestDate = LocalDate.of(2015, 12, 31);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		rule = groupBRulesFactory.getRuleX05();
		arresteeSegment = buildBaseGroupBSegment();
		GroupBArrestReport groupBArrestReport = (GroupBArrestReport) arresteeSegment.getParentReport();
		groupBArrestReport.setYearOfTape(2016);
		groupBArrestReport.setMonthOfTape(1);
		arrestDate = LocalDate.of(2016, 1, 31);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arrestDate = LocalDate.of(2016, 2, 1);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		arresteeSegment.setArrestDate(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRule665() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule665();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		GroupAIncidentReport incident = (GroupAIncidentReport) arresteeSegment.getParentReport();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		LocalDate incidentDateD = LocalDate.of(2016, 1, 31);
		ParsedObject<Date> incidentDate = new ParsedObject<>(Date.from(incidentDateD.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		incident.setIncidentDate(incidentDate);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		incidentDate = ParsedObject.getMissingParsedObject();
		incident.setIncidentDate(incidentDate);
		LocalDate arrestDate = LocalDate.of(2016, 1, 31);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		incidentDate = new ParsedObject<>(Date.from(incidentDateD.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		arrestDate = LocalDate.of(2016, 2, 1);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		incident.setIncidentDate(incidentDate);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arrestDate = LocalDate.of(2016, 1, 21);
		arresteeSegment.setArrestDate(Date.from(arrestDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._665, nibrsError.getNIBRSErrorCode());
		assertEquals("42", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getArrestDate(), nibrsError.getValue());
	}
	
	@Test
	public void testRule615() {
		atnFormatRuleTest(groupARulesFactory.getRuleX15(), NIBRSErrorCode._615);
	}

	@Test
	public void testRule617() {
		atnFormatRuleTest(groupARulesFactory.getRuleX17(), NIBRSErrorCode._617);
	}

	private void atnFormatRuleTest(Rule<ArresteeSegment> rule, NIBRSErrorCode errorCode) {
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArrestTransactionNumber(null);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArrestTransactionNumber("11-123-SC   ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArrestTransactionNumber("11-123*SC   ");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(errorCode, nibrsError.getNIBRSErrorCode());
		assertEquals("41", nibrsError.getDataElementIdentifier());
		arresteeSegment.setArrestTransactionNumber("11-123-SC");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRuleX01ForTypeOfArrest() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForTypeOfArrest();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setTypeOfArrest(TypeOfArrestCode.O.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setTypeOfArrest(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("43", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setTypeOfArrest("invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals("invalid", nibrsError.getValue());
	}
	
	@Test
	public void testRuleX01ForMultipleArresteeIndicator() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForMultipleArresteeIndicator();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setMultipleArresteeSegmentsIndicator(MultipleArresteeSegmentsIndicator.N.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setMultipleArresteeSegmentsIndicator(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("44", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setMultipleArresteeSegmentsIndicator("invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals("invalid", nibrsError.getValue());
		rule = groupBRulesFactory.getRuleX01ForMultipleArresteeIndicator();
		arresteeSegment = buildBaseGroupBSegment();
		arresteeSegment.setMultipleArresteeSegmentsIndicator(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRuleX01ForUCRArrestOffenseCode() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForUCRArrestOffenseCode();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._09A.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("45", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setUcrArrestOffenseCode("invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals("invalid", nibrsError.getValue());
	}
	
	@Test
	public void testRule670() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule670();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._09A.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._09C.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._670, nibrsError.getNIBRSErrorCode());
		assertEquals("45", nibrsError.getDataElementIdentifier());
		assertEquals(OffenseCode._09C.code, nibrsError.getValue());
	}
	
	@Test
	public void testRule760() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule760();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._09A.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		rule = groupBRulesFactory.getRule760();
		arresteeSegment = buildBaseGroupBSegment();
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._90A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._09A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._760, nibrsError.getNIBRSErrorCode());
		assertEquals("45", nibrsError.getDataElementIdentifier());
		assertEquals(OffenseCode._09A.code, nibrsError.getValue());
	}
	
	@Test
	public void testRuleX01ForArresteeWasArmedWith() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForArresteeWasArmedWith();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._01.code);
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("46", nibrsError.getDataElementIdentifier());
		assertArrayEquals(new String[] {null, null}, (String[]) nibrsError.getValue());
		arresteeSegment.setArresteeArmedWith(0, "invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertArrayEquals(new String[] {"invalid", null}, (String[]) nibrsError.getValue());
	}
	
	@Test
	public void testRuleX06ForArresteeWasArmedWith() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX06ForArresteeWasArmedWith();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._01.code);
		arresteeSegment.setArresteeArmedWith(1, ArresteeWasArmedWithCode._11.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(1, ArresteeWasArmedWithCode._01.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._606, nibrsError.getNIBRSErrorCode());
		assertEquals("46", nibrsError.getDataElementIdentifier());
		assertArrayEquals(new String[] {ArresteeWasArmedWithCode._01.code, ArresteeWasArmedWithCode._01.code}, (String[]) nibrsError.getValue());
	}
	
	@Test
	public void testRuleX07ForArresteeWasArmedWith() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX07ForArresteeWasArmedWith();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._01.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(1, ArresteeWasArmedWithCode._01.code);
		arresteeSegment.setArresteeArmedWith(1, ArresteeWasArmedWithCode._11.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._607, nibrsError.getNIBRSErrorCode());
		assertEquals("46", nibrsError.getDataElementIdentifier());
		assertArrayEquals(new String[] {ArresteeWasArmedWithCode._01.code, ArresteeWasArmedWithCode._11.code}, (String[]) nibrsError.getValue());
	}
	
	@Test
	public void testRuleX55() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX55();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._11.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAutomaticWeaponIndicator(0, AutomaticWeaponIndicatorCode.A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAutomaticWeaponIndicator(0, AutomaticWeaponIndicatorCode._blank.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._01.code);
		arresteeSegment.setAutomaticWeaponIndicator(0, null);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAutomaticWeaponIndicator(0, AutomaticWeaponIndicatorCode.A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._655, nibrsError.getNIBRSErrorCode());
		assertEquals("46", nibrsError.getDataElementIdentifier());
		assertEquals(ArresteeWasArmedWithCode._01, nibrsError.getValue());
	}
	
	@Test
	public void testRuleX54() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX54();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setArresteeArmedWith(0, ArresteeWasArmedWithCode._11.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAutomaticWeaponIndicator(0, AutomaticWeaponIndicatorCode.A.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAutomaticWeaponIndicator(0, "invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._654, nibrsError.getNIBRSErrorCode());
		assertEquals("46", nibrsError.getDataElementIdentifier());
		assertArrayEquals(new String[] {"invalid", null}, (String[]) nibrsError.getValue());
	}
	
	@Test
	public void testRuleX09() {

		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX09();

		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setAgeString("302 ");
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._609, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		assertEquals("302 ", nibrsError.getValue());

		arresteeSegment.setAgeString("2030");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		
	}
	
	@Test
	public void testRuleX10() {

		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX10();

		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setAgeString("3020");
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._610, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getAge(), nibrsError.getValue());

		arresteeSegment.setAgeString("2030");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		
	}
	
	@Test
	public void testRuleX22() {

		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX22();

		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setAgeString("0020");
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._622, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getAge(), nibrsError.getValue());

		arresteeSegment.setAgeString("2030");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		
	}
	
	@Test
	public void testRuleX01ForAge() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX01ForAge();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._601, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
		arresteeSegment.setAgeString("00  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRuleX04ForAge() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX04ForAge();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		arresteeSegment.setAgeString("AA  ");
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._604, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		arresteeSegment.setAgeString("22  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
	}
	
	@Test
	public void testRule761() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule761();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		rule = groupBRulesFactory.getRule761();
		arresteeSegment = buildBaseGroupBSegment();
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._90A.code);
		arresteeSegment.setAgeString("22  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setUcrArrestOffenseCode(OffenseCode._90I.code);
		arresteeSegment.setAgeString("16  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAgeString("1622");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAgeString("22  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._761, nibrsError.getNIBRSErrorCode());
		assertEquals("47", nibrsError.getDataElementIdentifier());
		assertEquals(arresteeSegment.getAge(), nibrsError.getValue());
	}
	
	@Test
	public void testRuleX01ForSex() {
		// nothing to do here.  this rule is amply tested for victim and offender.
	}
	
	@Test
	public void testRuleX01ForRace() {
		// nothing to do here.  this rule is amply tested for victim and offender.
	}
	
	@Test
	public void testRuleX04ForEthnicity() {
		// nothing to do here.  this rule is amply tested for victim and offender.
	}
	
	@Test
	public void testRuleX04ForResidentStatus() {
		// nothing to do here.  this rule is amply tested for victim.
	}
	
	@Test
	public void testRule667_758() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRule667_758();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setSex(SexCode.M.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setSex(SexCode.U.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._667, nibrsError.getNIBRSErrorCode());
		assertEquals("48", nibrsError.getDataElementIdentifier());
		assertEquals(SexCode.U.code, nibrsError.getValue());
		rule = groupBRulesFactory.getRule667_758();
		arresteeSegment = buildBaseGroupBSegment();
		arresteeSegment.setSex(SexCode.U.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._758, nibrsError.getNIBRSErrorCode());
	}
	
	@Test
	public void testRuleX04ForDispositionOfArresteeUnder18() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX04ForDispositionOfArresteeUnder18();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setDispositionOfArresteeUnder18(DispositionOfArresteeUnder18Code.H.code);
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setDispositionOfArresteeUnder18("invalid");
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._604, nibrsError.getNIBRSErrorCode());
		assertEquals("52", nibrsError.getDataElementIdentifier());
		assertEquals("invalid", nibrsError.getValue());
	}
	
	@Test
	public void testRuleX52() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX52();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setDispositionOfArresteeUnder18(DispositionOfArresteeUnder18Code.H.code);
		arresteeSegment.setAgeString("15  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setDispositionOfArresteeUnder18(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._652, nibrsError.getNIBRSErrorCode());
		assertEquals("52", nibrsError.getDataElementIdentifier());
		assertNull(nibrsError.getValue());
	}
	
	@Test
	public void testRuleX53() {
		Rule<ArresteeSegment> rule = groupARulesFactory.getRuleX53();
		ArresteeSegment arresteeSegment = buildBaseGroupASegment();
		NIBRSError nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setDispositionOfArresteeUnder18(DispositionOfArresteeUnder18Code.H.code);
		arresteeSegment.setAgeString("15  ");
		nibrsError = rule.apply(arresteeSegment);
		assertNull(nibrsError);
		arresteeSegment.setAgeString(null);
		nibrsError = rule.apply(arresteeSegment);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._653, nibrsError.getNIBRSErrorCode());
		assertEquals("52", nibrsError.getDataElementIdentifier());
		assertEquals(DispositionOfArresteeUnder18Code.H.code, nibrsError.getValue());
	}
	
	private ArresteeSegment buildBaseGroupASegment() {
		GroupAIncidentReport report = new GroupAIncidentReport();
		ReportSource source = new ReportSource();
		source.setSourceLocation(getClass().getName());
		source.setSourceName(getClass().getName());
		report.setSource(source);
		ArresteeSegment s = new ArresteeSegment(ArresteeSegment.GROUP_A_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(s);
		return s;
	}

	private ArresteeSegment buildBaseGroupBSegment() {
		GroupBArrestReport report = new GroupBArrestReport();
		ReportSource source = new ReportSource();
		source.setSourceLocation(getClass().getName());
		source.setSourceName(getClass().getName());
		report.setSource(source);
		ArresteeSegment s = new ArresteeSegment(ArresteeSegment.GROUP_B_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(s);
		return s;
	}

}

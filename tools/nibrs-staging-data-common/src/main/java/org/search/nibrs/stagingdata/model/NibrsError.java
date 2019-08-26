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
package org.search.nibrs.stagingdata.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(
	generator = ObjectIdGenerators.PropertyGenerator.class, 
	property = "nibrsErrorId")
@NamedEntityGraph(name="allNibrsErrorJoins", attributeNodes = {
        @NamedAttributeNode("segmentActionType"),
        @NamedAttributeNode("agency"),
        @NamedAttributeNode("nibrsErrorCodeType"),
})
public class NibrsError {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer nibrsErrorId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="segmentActionTypeTypeID") 
	private SegmentActionTypeType segmentActionType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="agencyId")
	private Agency agency; 

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nibrsErrorCodeTypeId")
	private NibrsErrorCodeType nibrsErrorCodeType; 
	
	
	private String monthOfTape; 
	private String yearOfTape; 
	private String sourceLocation;
	private String incidentIdentifier; 
	private String dataElement; 
	private String rejectedValue;
	private LocalDateTime nibrsErrorTimestamp;
	
	public SegmentActionTypeType getSegmentActionType() {
		return segmentActionType;
	}
	public void setSegmentActionType(SegmentActionTypeType segmentActionType) {
		this.segmentActionType = segmentActionType;
	}
	public Integer getNibrsErrorId() {
		return nibrsErrorId;
	}
	public void setNibrsErrorId(Integer nibrsErrorId) {
		this.nibrsErrorId = nibrsErrorId;
	}
	public Agency getAgency() {
		return agency;
	}
	public void setAgency(Agency agency) {
		this.agency = agency;
	}
	public NibrsErrorCodeType getNibrsErrorCodeType() {
		return nibrsErrorCodeType;
	}
	public void setNibrsErrorCodeType(NibrsErrorCodeType nibrsErrorCodeType) {
		this.nibrsErrorCodeType = nibrsErrorCodeType;
	}
	public String getMonthOfTape() {
		return monthOfTape;
	}
	public void setMonthOfTape(String monthOfTape) {
		this.monthOfTape = monthOfTape;
	}
	public String getYearOfTape() {
		return yearOfTape;
	}
	public void setYearOfTape(String yearOfTape) {
		this.yearOfTape = yearOfTape;
	}
	public String getSourceLocation() {
		return sourceLocation;
	}
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	public String getIncidentIdentifier() {
		return incidentIdentifier;
	}
	public void setIncidentIdentifier(String incidentIdentifier) {
		this.incidentIdentifier = incidentIdentifier;
	}
	public String getDataElement() {
		return dataElement;
	}
	public void setDataElement(String dataElement) {
		this.dataElement = dataElement;
	}
	public String getRejectedValue() {
		return rejectedValue;
	}
	public void setRejectedValue(String rejectedValue) {
		this.rejectedValue = rejectedValue;
	}
	public LocalDateTime getNibrsErrorTimestamp() {
		return nibrsErrorTimestamp;
	}
	public void setNibrsErrorTimestamp(LocalDateTime nibrsErrorTimestamp) {
		this.nibrsErrorTimestamp = nibrsErrorTimestamp;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nibrsErrorCodeType == null) ? 0 : nibrsErrorCodeType.hashCode());
		result = prime * result + ((agency == null) ? 0 : agency.hashCode());
		result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
		result = prime * result + ((incidentIdentifier == null) ? 0 : incidentIdentifier.hashCode());
		result = prime * result + ((monthOfTape == null) ? 0 : monthOfTape.hashCode());
		result = prime * result + ((nibrsErrorId == null) ? 0 : nibrsErrorId.hashCode());
		result = prime * result + ((nibrsErrorTimestamp == null) ? 0 : nibrsErrorTimestamp.hashCode());
		result = prime * result + ((rejectedValue == null) ? 0 : rejectedValue.hashCode());
		result = prime * result + ((segmentActionType == null) ? 0 : segmentActionType.hashCode());
		result = prime * result + ((sourceLocation == null) ? 0 : sourceLocation.hashCode());
		result = prime * result + ((yearOfTape == null) ? 0 : yearOfTape.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NibrsError other = (NibrsError) obj;
		if (nibrsErrorCodeType == null) {
			if (other.nibrsErrorCodeType != null)
				return false;
		} else if (!nibrsErrorCodeType.equals(other.nibrsErrorCodeType))
			return false;
		if (agency == null) {
			if (other.agency != null)
				return false;
		} else if (!agency.equals(other.agency))
			return false;
		if (dataElement == null) {
			if (other.dataElement != null)
				return false;
		} else if (!dataElement.equals(other.dataElement))
			return false;
		if (incidentIdentifier == null) {
			if (other.incidentIdentifier != null)
				return false;
		} else if (!incidentIdentifier.equals(other.incidentIdentifier))
			return false;
		if (monthOfTape == null) {
			if (other.monthOfTape != null)
				return false;
		} else if (!monthOfTape.equals(other.monthOfTape))
			return false;
		if (nibrsErrorId == null) {
			if (other.nibrsErrorId != null)
				return false;
		} else if (!nibrsErrorId.equals(other.nibrsErrorId))
			return false;
		if (nibrsErrorTimestamp == null) {
			if (other.nibrsErrorTimestamp != null)
				return false;
		} else if (!nibrsErrorTimestamp.equals(other.nibrsErrorTimestamp))
			return false;
		if (rejectedValue == null) {
			if (other.rejectedValue != null)
				return false;
		} else if (!rejectedValue.equals(other.rejectedValue))
			return false;
		if (segmentActionType == null) {
			if (other.segmentActionType != null)
				return false;
		} else if (!segmentActionType.equals(other.segmentActionType))
			return false;
		if (sourceLocation == null) {
			if (other.sourceLocation != null)
				return false;
		} else if (!sourceLocation.equals(other.sourceLocation))
			return false;
		if (yearOfTape == null) {
			if (other.yearOfTape != null)
				return false;
		} else if (!yearOfTape.equals(other.yearOfTape))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "NibrsError [nibrsErrorId=" + nibrsErrorId + ", segmentActionType=" + segmentActionType + ", agency="
				+ agency + ", NibrsErrorCodeType=" + nibrsErrorCodeType + ", monthOfTape=" + monthOfTape
				+ ", yearOfTape=" + yearOfTape + ", sourceLocation=" + sourceLocation + ", incidentIdentifier="
				+ incidentIdentifier + ", dataElement=" + dataElement + ", rejectedValue=" + rejectedValue
				+ ", nibrsErrorTimestamp=" + nibrsErrorTimestamp + "]";
	}
	
}

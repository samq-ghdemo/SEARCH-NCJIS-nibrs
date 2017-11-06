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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
public class ArresteeWasArmedWithType {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer arresteeWasArmedWithTypeId; 
	
	private String arresteeWasArmedWithCode; 
	private String arresteeWasArmedWithDescription; 
	
	@OneToMany(mappedBy = "arresteeWasArmedWithType")
	private Set<ArresteeSegmentWasArmedWith> arresteeSegmentWasArmedWith;
	
	@OneToMany(mappedBy = "arresteeWasArmedWithType")
	private Set<ArrestReportSegmentWasArmedWith> arrestReportSegmentWasArmedWith;
	
	public ArresteeWasArmedWithType() {
		super();
	}

	public ArresteeWasArmedWithType(Integer arresteeWasArmedWithTypeId) {
		super();
		this.arresteeWasArmedWithTypeId = arresteeWasArmedWithTypeId;
	}

	public String toString(){
		return ReflectionToStringBuilder.toStringExclude(this, "arresteeSegmentWasArmedWith", "arrestReportSegmentWasArmedWith");
	}

	public Integer getArresteeWasArmedWithTypeId() {
		return arresteeWasArmedWithTypeId;
	}

	public void setArresteeWasArmedWithTypeId(Integer arresteeWasArmedWithTypeId) {
		this.arresteeWasArmedWithTypeId = arresteeWasArmedWithTypeId;
	}

	public String getArresteeWasArmedWithCode() {
		return arresteeWasArmedWithCode;
	}

	public void setArresteeWasArmedWithCode(String arresteeWasArmedWithCode) {
		this.arresteeWasArmedWithCode = arresteeWasArmedWithCode;
	}

	public String getArresteeWasArmedWithDescription() {
		return arresteeWasArmedWithDescription;
	}

	public void setArresteeWasArmedWithDescription(String arresteeWasArmedWithDescription) {
		this.arresteeWasArmedWithDescription = arresteeWasArmedWithDescription;
	}

}
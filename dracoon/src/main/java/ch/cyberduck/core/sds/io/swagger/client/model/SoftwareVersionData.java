/*
 * DRACOON
 * REST Web Services for DRACOON<br>Version: 4.8.0-LTS  - built at: 2018-05-03 15:44:37<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a>
 *
 * OpenAPI spec version: 4.8.0-LTS
 * Contact: develop@dracoon.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ch.cyberduck.core.sds.io.swagger.client.model;

/*
 * Copyright (c) 2002-2018 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import org.joda.time.DateTime;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * SoftwareVersionData
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-05-23T09:31:14.222+02:00")
public class SoftwareVersionData {
  @JsonProperty("restApiVersion")
  private String restApiVersion = null;

  @JsonProperty("sdsServerVersion")
  private String sdsServerVersion = null;

  @JsonProperty("buildDate")
  private DateTime buildDate = null;

  @JsonProperty("scmRevisionNumber")
  private String scmRevisionNumber = null;

  public SoftwareVersionData restApiVersion(String restApiVersion) {
    this.restApiVersion = restApiVersion;
    return this;
  }

   /**
   * REST API version
   * @return restApiVersion
  **/
  @ApiModelProperty(example = "4.4.1", required = true, value = "REST API version")
  public String getRestApiVersion() {
    return restApiVersion;
  }

  public void setRestApiVersion(String restApiVersion) {
    this.restApiVersion = restApiVersion;
  }

  public SoftwareVersionData sdsServerVersion(String sdsServerVersion) {
    this.sdsServerVersion = sdsServerVersion;
    return this;
  }

   /**
   * DRACOON server version
   * @return sdsServerVersion
  **/
  @ApiModelProperty(example = "4.4.1", required = true, value = "DRACOON server version")
  public String getSdsServerVersion() {
    return sdsServerVersion;
  }

  public void setSdsServerVersion(String sdsServerVersion) {
    this.sdsServerVersion = sdsServerVersion;
  }

  public SoftwareVersionData buildDate(DateTime buildDate) {
    this.buildDate = buildDate;
    return this;
  }

   /**
   * Build date
   * @return buildDate
  **/
  @ApiModelProperty(example = "2018-01-01T00:00:00", required = true, value = "Build date")
  public DateTime getBuildDate() {
    return buildDate;
  }

  public void setBuildDate(DateTime buildDate) {
    this.buildDate = buildDate;
  }

  public SoftwareVersionData scmRevisionNumber(String scmRevisionNumber) {
    this.scmRevisionNumber = scmRevisionNumber;
    return this;
  }

   /**
   * Revision number
   * @return scmRevisionNumber
  **/
  @ApiModelProperty(required = true, value = "Revision number")
  public String getScmRevisionNumber() {
    return scmRevisionNumber;
  }

  public void setScmRevisionNumber(String scmRevisionNumber) {
    this.scmRevisionNumber = scmRevisionNumber;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SoftwareVersionData softwareVersionData = (SoftwareVersionData) o;
    return Objects.equals(this.restApiVersion, softwareVersionData.restApiVersion) &&
        Objects.equals(this.sdsServerVersion, softwareVersionData.sdsServerVersion) &&
        Objects.equals(this.buildDate, softwareVersionData.buildDate) &&
        Objects.equals(this.scmRevisionNumber, softwareVersionData.scmRevisionNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restApiVersion, sdsServerVersion, buildDate, scmRevisionNumber);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SoftwareVersionData {\n");

    sb.append("    restApiVersion: ").append(toIndentedString(restApiVersion)).append("\n");
    sb.append("    sdsServerVersion: ").append(toIndentedString(sdsServerVersion)).append("\n");
    sb.append("    buildDate: ").append(toIndentedString(buildDate)).append("\n");
    sb.append("    scmRevisionNumber: ").append(toIndentedString(scmRevisionNumber)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

/*
 * DRACOON
 * REST Web Services for DRACOON<br>Version: 4.13.0  - built at: 2019-08-07 15:10:42<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a>
 *
 * OpenAPI spec version: 4.13.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request model for updating auth token settings
 */
@ApiModel(description = "Request model for updating auth token settings")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-09-13T14:08:20.178+02:00")
public class UpdateAuthTokenRestrictions {
    @JsonProperty("overwriteEnabled")
    private Boolean overwriteEnabled = null;

    @JsonProperty("accessTokenValidity")
    private Integer accessTokenValidity = null;

    @JsonProperty("refreshTokenValidity")
    private Integer refreshTokenValidity = null;

    public UpdateAuthTokenRestrictions overwriteEnabled(Boolean overwriteEnabled) {
        this.overwriteEnabled = overwriteEnabled;
        return this;
    }

    /**
     * Defines if OAuth token restrictions are enabled
     *
     * @return overwriteEnabled
     **/
    @ApiModelProperty(required = true, value = "Defines if OAuth token restrictions are enabled")
    public Boolean isOverwriteEnabled() {
        return overwriteEnabled;
    }

    public void setOverwriteEnabled(Boolean overwriteEnabled) {
        this.overwriteEnabled = overwriteEnabled;
    }

    public UpdateAuthTokenRestrictions accessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
        return this;
    }

    /**
     * Restricted OAuth access token validity (in seconds)
     *
     * @return accessTokenValidity
     **/
    @ApiModelProperty(value = "Restricted OAuth access token validity (in seconds)")
    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public UpdateAuthTokenRestrictions refreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
        return this;
    }

    /**
     * Restricted OAuth refresh token validity (in seconds)
     *
     * @return refreshTokenValidity
     **/
    @ApiModelProperty(value = "Restricted OAuth refresh token validity (in seconds)")
    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateAuthTokenRestrictions updateAuthTokenRestrictions = (UpdateAuthTokenRestrictions) o;
        return Objects.equals(this.overwriteEnabled, updateAuthTokenRestrictions.overwriteEnabled) &&
            Objects.equals(this.accessTokenValidity, updateAuthTokenRestrictions.accessTokenValidity) &&
            Objects.equals(this.refreshTokenValidity, updateAuthTokenRestrictions.refreshTokenValidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overwriteEnabled, accessTokenValidity, refreshTokenValidity);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UpdateAuthTokenRestrictions {\n");

        sb.append("    overwriteEnabled: ").append(toIndentedString(overwriteEnabled)).append("\n");
        sb.append("    accessTokenValidity: ").append(toIndentedString(accessTokenValidity)).append("\n");
        sb.append("    refreshTokenValidity: ").append(toIndentedString(refreshTokenValidity)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if(o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}


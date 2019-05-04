/*
 *  Copyright (c) 2012 - 2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ch.frimtec.teamcity.plugin.inspectionnotification;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("inspection-notification")
public final class InspectionNotificationConfiguration {

    public static final String INSPECTION_ADMIN_GROUP_NAME_KEY = "inspectionAdminGroupName";
    public static final String BITBUCKET_ROOT_URL_KEY = "bitbucketRootUrl";
    public static final String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
    public static final String EMAIL_SMTP_HOST_KEY = "emailSmtpHost";
    public static final String EMAIL_SMTP_PORT_KEY = "emailSmtpPort";

    @XStreamAlias(INSPECTION_ADMIN_GROUP_NAME_KEY)
    private String inspectionAdminGroupName = "inspection-admin";
    @XStreamAlias(BITBUCKET_ROOT_URL_KEY)
    private String bitbucketRootUrl = "";
    @XStreamAlias(EMAIL_FROM_ADDRESS_KEY)
    private String emailFromAddress = "teamcity@localhost";
    @XStreamAlias(EMAIL_SMTP_HOST_KEY)
    private String emailSmtpHost = "localhost";
    @XStreamAlias(EMAIL_SMTP_PORT_KEY)
    private int emailSmtpPort = 25;

    public String getInspectionAdminGroupName() {
        return inspectionAdminGroupName;
    }

    public void setInspectionAdminGroupName(String inspectionAdminGroupName) {
        this.inspectionAdminGroupName = inspectionAdminGroupName;
    }

    public String getBitbucketRootUrl() {
        return bitbucketRootUrl;
    }

    public void setBitbucketRootUrl(String bitbucketRootUrl) {
        this.bitbucketRootUrl = bitbucketRootUrl;
    }

    public String getEmailFromAddress() {
        return emailFromAddress;
    }

    public void setEmailFromAddress(String emailFromAddress) {
        this.emailFromAddress = emailFromAddress;
    }

    public String getEmailSmtpHost() {
        return emailSmtpHost;
    }

    public void setEmailSmtpHost(String emailSmtpHost) {
        this.emailSmtpHost = emailSmtpHost;
    }

    public int getEmailSmtpPort() {
        return emailSmtpPort;
    }

    public void setEmailSmtpPort(int emailSmtpPort) {
        this.emailSmtpPort = emailSmtpPort;
    }
}

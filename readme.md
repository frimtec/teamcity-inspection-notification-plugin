# TeamCity-Inspection-Notification-Plugin
[![Build Status](https://travis-ci.org/frimtec/teamcity-inspection-notification-plugin.svg?branch=master)](https://travis-ci.org/frimtec/teamcity-inspection-notification-plugin) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.frimtec/teamcity-inspection-notification-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.frimtec/teamcity-inspection-notification-plugin) 
[![Coverage Status](https://coveralls.io/repos/github/frimtec/teamcity-inspection-notification-plugin/badge.svg?branch=master)](https://travis-ci.org/frimtec/teamcity-inspection-notification-plugin?branch=master) 
[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

TeamCity server-side plugin that notifies committers about newly introduced code inspection violations.

![Screen shot of app](images/email.png "Screen shot of the notification")

# Installation

Install it directly from JetBrains TeamCity-Plugin-Repository [inspection-violation-notification](https://plugins.jetbrains.com/plugin/12382-inspection-violation-notification).

Compatibility:
* Release 1.0 was tested against TeamCity 2018.2.

# Configuration

On TeamCity, as an administrator, configure the plugin on the administration tab "Inspection Violation Notfification".

# Future Improvements

* Replace own email SMTP configuration.
* Replace use of deprecated SQLRunner API.

# Change log

## Version 1.0.3
* Bugfix: Prevent violation duplicates in generated emails.

## Version 1.0.2
* Rename plugin to be compliant with JetBrains naming schema.

## Version 1.0.1
* Initial release.

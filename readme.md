# TeamCity-Inspection-Notification-Plugin
[![Build Status](https://travis-ci.com/frimtec/teamcity-inspection-notification-plugin.svg?branch=master)](https://travis-ci.com/frimtec/teamcity-inspection-notification-plugin) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.frimtec/teamcity-inspection-notification-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.frimtec/teamcity-inspection-notification-plugin) 
[![Coverage Status](https://coveralls.io/repos/github/frimtec/teamcity-inspection-notification-plugin/badge.svg?branch=master)](https://coveralls.io/github/frimtec/teamcity-inspection-notification-plugin?branch=master)
[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

TeamCity server-side plugin that notifies committers about newly introduced code inspection violations.

![Screen shot of app](images/email.png "Screen shot of the notification")

# Installation

Install it directly from JetBrains TeamCity-Plugin-Repository [inspection-violation-notification](https://plugins.jetbrains.com/plugin/12382-inspection-violation-notification).

Compatibility:
* Release 1.0 was tested against TeamCity 2018.2 and 2019.1.

# Configuration

On TeamCity, as an administrator, configure the plugin on the administration tab "Inspection Violation Notification".

# Future Improvements

* Replace own email SMTP configuration.
* Replace use of deprecated SQLRunner API.

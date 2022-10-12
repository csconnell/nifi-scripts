NiFi Scripts
==========================

Background
----------

This repository holds a number of scripts that can be utilized within NiFi or as a way to test NiFi functionality.  For instance, scripts can be utilized within ExecuteScript or InvokeScriptedProcessor components.


Running The Scripts
----------------

How a script can be run depends upon the type of script and the intended run location. Scripts that are intended to run within a InvokeScriptedProcessor or similar processor will only be able to run within a NiFi processor, as only the NiFi environment will provide the needed context.

Other scripts may be able to run within another run time environment.

Each script included in this repository are noted below and include a description of what they do and where they can run.

| Script| Language| Notes|
| ---|:---:|:---|
|narDirectoryCompare |Groovy |Used to perform a 3 way compare on the working, extention, and standard NAR directories to determine if NiFi needs a restart because of uncleaned working files due to deleting a customer NAR. </p>Runs in an ExecuteGroovyScript Processor </p> Can not run in current form in a standalone Groovy environment, as it requires NiFi attributes.|
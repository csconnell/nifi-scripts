/**
    This is a Groovy Script for a very particular directory / file compare.

    While the script could be more generic, in this form it is intended for the singular
    purpose of comparing the what has been unpacked in the NiFi working directory (NAR Working Directory)
    to what is in the Extension directory (NAR Extension Directory) where we put custom NARs to determine
    if a NiFi restart is needed to remove previously unpacked NARs.

    We also need to do a 3 way compare to determine that, as the working directory will also contain
    all of the unpacked NARs from the standard NAR Directory (NAR Directory).

    This script takes the parameters for all three directories and then performs the following operations:

    - Get the names of all of the directories in the NAR Working Directory
    - Strip "-unpacked" off of the name so that we can perform a name compare
    - Get the names of all of the files from the NAR extension and NAR standard directories
    - Remove all of the names from the Extention and Standard directories from the Work Directory
    - What is left, is what is different.
**/

import static groovy.io.FileType.DIRECTORIES;
import static groovy.io.FileType.FILES;

// Get the flowfile
def flowFile = session.get();

// This expects a flowfile coming in, if there isn't one, we are done.
if (flowFile == null) {
    return;
}

// Define our Collections
def workCollection = [];
def narCollection = [];
def customNarCollection = [];

// Capture the directories
def workDir = new File(flowFile.getAttribute('NAR Working Directory'));
def narDir = new File(flowFile.getAttribute('NAR Directory'));
def extensionDir = new File(flowFile.getAttribute('NAR Extension Directory'));

// Get all of the NARs that are in the Work Dir, stripping "-unpacked" from the name
workDir.eachFile( DIRECTORIES )
{
    workCollection.add( it.name.minus("-unpacked"));
}

// Get all of the NAR Files from the NAR directory
// there might be some other files here (JARs), but that is ok
narDir.eachFile( FILES )
{
    narCollection.add( it.name );
}

// Same deal for what is in the extension directory
extensionDir.eachFile( FILES )
{
    customNarCollection.add( it.name );
}

/*
    Now take away whatever we find in NAR and Extension from Work

    Whatever is left over means we have something in Extensions
    that has been deleted and we need to restart NiFi to clear out
    the Working directory.
*/
def workAfterNar =  workCollection.minus(narCollection).minus(customNarCollection);

// We want some JSON
def json = groovy.json.JsonOutput.toJson(workAfterNar)

// We are going to output JSON
flowFile = session.putAttribute( flowFile, "mime.type", "application/json" );

// write it all out
session.write(flowFile,
    { outputStream ->
        outputStream.write( json.getBytes() )
    } as OutputStreamCallback
)

// Transition to success
session.transfer(flowFile, REL_SUCCESS)

// TODO:  Need to handle the error / fail case.
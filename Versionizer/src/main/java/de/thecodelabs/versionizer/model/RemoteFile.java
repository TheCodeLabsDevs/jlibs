package de.thecodelabs.versionizer.model;

public class RemoteFile {

    public enum FileType {
        JAR, EXE;
    }

    private String name;
    private FileType fileType;

}

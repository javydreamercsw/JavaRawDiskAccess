package com.smithnephew.rawdisk;

import com.smithnephew.rf20000.sd.card.manager.disk.DriveTypeEnum;
import java.io.File;

/**
 * The drive information.
 */
public final class Drive {

    /**
     * File system on the logical disk. Example: NTFS. null if not known.
     */
    private final String fileSystem;
    /**
     * Value that corresponds to the type of disk drive this logical disk
     * represents.
     */
    private final DriveTypeEnum driveType;
    /**
     * The Java file, e.g. "C:\". Never null.
     */
    private final File file;

    public Drive(String fileSystem, DriveTypeEnum driveType, File file) {
        this.fileSystem = fileSystem;
        this.driveType = driveType;
        this.file = file;
    }

    @Override
    public String toString() {
        return "Drive{" + getFile() + ", " + getDriveType() + ", fileSystem=" + getFileSystem() + "}";
    }

    /**
     * @return the fileSystem
     */
    public String getFileSystem() {
        return fileSystem;
    }

    /**
     * @return the driveType
     */
    public DriveTypeEnum getDriveType() {
        return driveType;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

}

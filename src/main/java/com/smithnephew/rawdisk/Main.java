package com.smithnephew.rawdisk;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.JacobObject;
import com.jacob.com.Variant;
import com.smithnephew.rf20000.sd.card.manager.disk.DriveTypeEnum;
import com.smithnephew.rf20000.sd.card.manager.disk.HasNativeValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<Drive> drives = getDrives();

        drives.forEach((drive) -> {
            if (drive.getDriveType() == DriveTypeEnum.RemovableDisk) {
                System.out.println(drive.toString());
                try {
                    File diskRoot = new File("\\\\.\\" + drive.getFile());
                    RandomAccessFile diskAccess
                            = new RandomAccessFile(diskRoot, "r");
                    byte[] content = new byte[512];
                    diskAccess.readFully(content);
                    System.out.println(Arrays.toString(content));
                } catch (FileNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static <T extends Enum<T> & HasNativeValue> T
            fromNative(Class<T> clazz, int value) {
        for (T c : clazz.getEnumConstants()) {
            if (c.getNativeValue() == value) {
                return c;
            }
        }
        return null;
    }

    /**
     * Lists all available Windows drives without actually touching them. This
     * call should not block on cd-roms, floppies, network drives etc.
     *
     * @return a list of drives, never null, may be empty.
     */
    public static List<Drive> getDrives() {
        List<Drive> result = new ArrayList<>();
        ActiveXComponent axWMI = new ActiveXComponent("winmgmts://");

        try {
            Variant devices = axWMI.invoke("ExecQuery",
                    new Variant("Select DeviceID,DriveType,FileSystem from Win32_LogicalDisk"));
            EnumVariant deviceList = new EnumVariant(devices.toDispatch());
            while (deviceList.hasMoreElements()) {
                Dispatch item = deviceList.nextElement().toDispatch();
                String drive = Dispatch.call(item, "DeviceID").toString()
                        .toUpperCase();
                File file = new File(drive + "/");
                DriveTypeEnum driveType = fromNative(DriveTypeEnum.class,
                        Dispatch.call(item, "DriveType").getInt());
                String fileSystem = Dispatch.call(item, "FileSystem").toString();
                result.add(new Drive(fileSystem, driveType, file));
            }

            return result;
        } finally {
            closeQuietly(axWMI);
        }
    }

    private static void closeQuietly(JacobObject jacobObject) {
        try {
            jacobObject.safeRelease();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

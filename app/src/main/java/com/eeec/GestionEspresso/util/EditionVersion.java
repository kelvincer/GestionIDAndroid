package com.eeec.GestionEspresso.util;

/**
 * Created by rrodriguez on 7/07/16.
 */
public enum EditionVersion {

    AM(1),
    PM(2),
    FLASH(3);

    private int versionId;

    private EditionVersion(int versionId){
        this.versionId = versionId;
    }

    public static EditionVersion get(int versionId){
        for(EditionVersion version:  EditionVersion.values() ){
            if( version.versionId== versionId) return version;
        }
        return null;
    }

}

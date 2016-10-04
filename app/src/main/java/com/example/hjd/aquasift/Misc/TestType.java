package com.example.hjd.aquasift.Misc;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by HJD on 9/24/2016.
 */

public class TestType implements Serializable{

    public static final String TESTS_FILE_NAME = "SavedTests.ser";

    public String testName;
    public int[] settings;


    public TestType(String testName, int[] settings) {
        this.testName = testName;
        this.settings = new int[settings.length];
        System.arraycopy(settings, 0, this.settings, 0, settings.length);
    }
}

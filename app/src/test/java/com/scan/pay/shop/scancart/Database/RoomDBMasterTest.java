package com.scan.pay.shop.scancart.Database;

import android.app.Application;
import android.content.Context;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import kotlin.jvm.Throws;

@RunWith(JUnit4.class)
public class RoomDBMasterTest {



    @After
    public void setUp() throws Exception {
        Context context = Application.class.newInstance().getApplicationContext();
    }

    @Test
    public void productDAO() {
    }
}
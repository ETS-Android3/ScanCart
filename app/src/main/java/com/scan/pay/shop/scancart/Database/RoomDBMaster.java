package com.scan.pay.shop.scancart.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.scan.pay.shop.scancart.Models.ProductModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = ProductModel.class, version = 1)
public abstract class RoomDBMaster extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile RoomDBMaster INSTANCE;
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                productDAO dao = INSTANCE.productDAO();
                dao.deleteAll();

                ProductModel productModel = new ProductModel();
                dao.insertProduct(productModel);
            });
        }
    };

    public static RoomDBMaster getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDBMaster.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RoomDBMaster.class, "QRProduct_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract productDAO productDAO();
}

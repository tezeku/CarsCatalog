package com.example.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String ALL_CARS = "ALL_CARS";
    private static final String LIKED_CARS = "LIKED_CARS";
    private static final String COLUMN_IMAGE = "COLUMN_IMAGE";
    private static final String COLUMN_BRAND = "COLUMN_BRAND";
    private static final String COLUMN_MODEL = "COLUMN_MODEL";
    private static final String COLUMN_YEAR = "COLUMN_YEAR";
    private static final String COLUMN_PRICE = "COLUMN_PRICE";
    private static final String COLUMN_INFO = "COLUMN_INFO";

    public DBHelper(Context context) {
        super(context, "Cars.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ALL_CARS + " (" + COLUMN_IMAGE + " TEXT, " + COLUMN_BRAND + " TEXT, " + COLUMN_MODEL + " TEXT, " + COLUMN_YEAR + " INTEGER, " + COLUMN_PRICE + " INTEGER, " + COLUMN_INFO + " TEXT);");
        db.execSQL("CREATE TABLE " + LIKED_CARS + " (" + COLUMN_IMAGE + " TEXT, " + COLUMN_BRAND + " TEXT, " + COLUMN_MODEL + " TEXT, " + COLUMN_YEAR + " INTEGER, " + COLUMN_PRICE + " INTEGER, " + COLUMN_INFO + " TEXT);");
    }

    public void setDataInDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<CarItem> initialCars = new ArrayList<>();
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-vertis-journal/4080458/001.jpg_1626940945546/orig", "Toyota", "Supra", 1995, 10000000, "ДВС: 2JZ-GTE 320 л.с.\nПробег: 13000"));
        initialCars.add(new CarItem("https://api2.ma.ru/images/models/toyota/rav4noviy/28657/28657_medium.jpg", "Toyota", "RAV4", 2021, 3200000, "ДВС: 149 л.с."));
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-verba/3587101/2a00000178875ee704791437db034a6502a8/cattouchret", "Toyota", "Land Cruiser Prado", 2021, 4900000, "ДВС: 204 л.с.\nПробег: 6000"));
        initialCars.add(new CarItem("https://porsche-avtodom.ru/upload/iblock/5fb/2ph4j6itaovpygqv0xme9x8mj2ty0c4z.jpg", "Porsche", "Taycan", 1995, 20000000, "ДВС: 476 л.с.\nПробег: 300"));
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-verba/787013/2a000001675ec26d44c5fc838f55f253d508/cattouchret", "Porsche", "911", 2018, 13200000, "ДВС: 385 л.с.\nПробег: 200"));
        initialCars.add(new CarItem("https://www.porsche-moscow.ru/photos/cars/custom/454507_uJOy7ru8F2exgvucpESPhTruABbmXi7x.jpg.optimized.jpg", "Porsche", "911 Turbo S", 2021, 23900000, "ДВС: 650 л.с.\nПробег: 100"));
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-autoru-vos/2068358/0875c36755ebafb6a4e225636e38b76b/456x342", "BMW", "Z3 coupe", 2000, 2000000, "ДВС: 192 л.с.\nПробег: 17000"));
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-autoru-vos/5592926/600d057f7f776eff3673a837a68ba6b4/456x342", "BMW", "M4", 2021, 12200000, "ДВС: 510 л.с.\nПробег: 10000"));
        initialCars.add(new CarItem("https://avatars.mds.yandex.net/get-autoru-vos/1977683/2f179491ca15118ee50660b2d64620d2/456x342", "BMW", "M5", 2018, 12300000, "ДВС: 600 л.с.\nПробег: 25000"));

        for (CarItem item : initialCars) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IMAGE, item.getCarThumbnail());
            values.put(COLUMN_BRAND, item.getCarBrand());
            values.put(COLUMN_MODEL, item.getCarModel());
            values.put(COLUMN_YEAR, item.getCarYear());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_INFO, item.getCarInfo());

            db.insert(ALL_CARS, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALL_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + LIKED_CARS);
        onCreate(db);
    }

    public void removeLiked() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LIKED_CARS, null, null);
        db.close();
    }

    public void addLiked(CarItem carItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_IMAGE, carItem.getCarThumbnail());
        cv.put(COLUMN_BRAND, carItem.getCarBrand());
        cv.put(COLUMN_MODEL, carItem.getCarModel());
        cv.put(COLUMN_YEAR, carItem.getCarYear());
        cv.put(COLUMN_PRICE, carItem.getPrice());
        cv.put(COLUMN_INFO, carItem.getCarInfo());

        db.insert(LIKED_CARS, null, cv);

        db.close();
    }

    public void removeOneLiked(CarItem carItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Параметры для удаления
        String whereClause = COLUMN_BRAND + " = ? AND " + COLUMN_MODEL + " = ? AND " + COLUMN_YEAR + " = ?";
        String[] whereArgs = {
                carItem.getCarBrand(),
                carItem.getCarModel(),
                String.valueOf(carItem.getCarYear())
        };

        // Удаление записи
        db.delete(LIKED_CARS, whereClause, whereArgs);

        // Закрытие базы данных
        db.close();
    }



    public CarItem searchInLiked(CarItem carItem) {
        SQLiteDatabase db = this.getReadableDatabase();
        CarItem foundCarItem = null;

        // Параметры для запроса
        String selection = COLUMN_BRAND + " = ? AND " + COLUMN_MODEL + " = ? AND " + COLUMN_YEAR + " = ?";
        String[] selectionArgs = {
                carItem.getCarBrand(),
                carItem.getCarModel(),
                String.valueOf(carItem.getCarYear())
        };

        // Выполнение запроса
        Cursor cursor = db.query(
                LIKED_CARS,   // Таблица
                null,         // Все столбцы
                selection,    // Столбцы для предложения WHERE
                selectionArgs,// Значения для предложения WHERE
                null,         // Группировка строк
                null,         // Фильтр строк по группам
                null          // Порядок сортировки
        );

        // Проверка, найден ли элемент
        if (cursor != null && cursor.moveToFirst()) {
            int carThumbnailIndex = cursor.getColumnIndex(COLUMN_IMAGE);
            int carBrandIndex = cursor.getColumnIndex(COLUMN_BRAND);
            int carModelIndex = cursor.getColumnIndex(COLUMN_MODEL);
            int carYearIndex = cursor.getColumnIndex(COLUMN_YEAR);
            int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int carInfoIndex = cursor.getColumnIndex(COLUMN_INFO);

            // Извлечение данных из курсора
            String carThumbnail = cursor.getString(carThumbnailIndex);
            String carBrand = cursor.getString(carBrandIndex);
            String carModel = cursor.getString(carModelIndex);
            int carYear = cursor.getInt(carYearIndex);
            int price = cursor.getInt(priceIndex);
            String carInfo = cursor.getString(carInfoIndex);

            // Создание CarItem с данными из курсора
            foundCarItem = new CarItem(carThumbnail, carBrand, carModel, carYear, price, carInfo);
        }

        // Закрытие курсора и базы данных
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return foundCarItem;
    }

    public List<CarItem> getData() {
        List<CarItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ALL_CARS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
               int id_im = cursor.getColumnIndex(COLUMN_IMAGE);
               int id_br = cursor.getColumnIndex(COLUMN_BRAND);
               int id_mod = cursor.getColumnIndex(COLUMN_MODEL);
               int id_year = cursor.getColumnIndex(COLUMN_YEAR);
               int id_price = cursor.getColumnIndex(COLUMN_PRICE);
               int id_in = cursor.getColumnIndex(COLUMN_INFO);

               CarItem carItem = new CarItem(cursor.getString(id_im), cursor.getString(id_br), cursor.getString(id_mod), cursor.getInt(id_year), cursor.getInt(id_price), cursor.getString(id_in));
               list.add(carItem);
            } while (cursor.moveToNext());
        }

        db.close();
        return list;
    }

    public List<CarItem> getLiked() {
        List<CarItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LIKED_CARS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id_im = cursor.getColumnIndex(COLUMN_IMAGE);
                int id_br = cursor.getColumnIndex(COLUMN_BRAND);
                int id_mod = cursor.getColumnIndex(COLUMN_MODEL);
                int id_year = cursor.getColumnIndex(COLUMN_YEAR);
                int id_price = cursor.getColumnIndex(COLUMN_PRICE);
                int id_in = cursor.getColumnIndex(COLUMN_INFO);

                CarItem carItem = new CarItem(cursor.getString(id_im), cursor.getString(id_br), cursor.getString(id_mod), cursor.getInt(id_year), cursor.getInt(id_price), cursor.getString(id_in));
                list.add(carItem);
            } while (cursor.moveToNext());
        }

        db.close();
        return list;
    }
}

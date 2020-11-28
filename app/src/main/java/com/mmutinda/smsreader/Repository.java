package com.mmutinda.smsreader;

import android.app.Application;
import android.os.AsyncTask;

import com.mmutinda.smsreader.daos.SmsDao;
import com.mmutinda.smsreader.entities.SmsEntity;

import java.util.List;

public class Repository {

    private static SmsDao smsDao;

    public Repository(Application application) {

        AppDatabase appDatabase = AppDatabase.getDatabase(application);

        smsDao = appDatabase.smsDao();

    }

    public void insertSingleSms(SmsEntity smsEntities) {
        new InsertSingleSmsDbAsync(smsDao).execute(smsEntities);
    }

    public void insertManySms(List<SmsEntity> smsEntities) {
        new InsertMultipleSmsDbAsync(smsDao).execute(smsEntities);
    }

    public void updateAllToSynced() {

        new updateAllSynced(smsDao).execute();
    }

    private class updateAllSynced extends AsyncTask<List<SmsEntity>, Void, Void> {

        private SmsDao dataDao;

        updateAllSynced(SmsDao dao) {
            this.dataDao = dao;
        }

        @Override
        protected Void doInBackground(List<SmsEntity>... params) {
            try {
                dataDao.updateall();

            } catch (Exception e) {
            }

            return null;
        }

    }

    private class InsertMultipleSmsDbAsync extends AsyncTask<List<SmsEntity>, Void, Void> {

        private SmsDao dataDao;

        InsertMultipleSmsDbAsync(SmsDao dao) {
            this.dataDao = dao;
        }

        @Override
        protected Void doInBackground(List<SmsEntity>... params) {
            try {
                dataDao.insertAll(params[0]);

            } catch (Exception e) {
            }

            return null;
        }

    }

    private class InsertSingleSmsDbAsync extends AsyncTask<SmsEntity, Void, Void> {

        private SmsDao dataDao;

        InsertSingleSmsDbAsync(SmsDao dao) {
            this.dataDao = dao;
        }

        @Override
        protected Void doInBackground(SmsEntity... params) {
            try {
                dataDao.insert(params[0]);

            } catch (Exception e) {
            }

            return null;
        }

    }

    public List<SmsEntity> getUnsyncedDataSynchronously() {
        return smsDao.getAll();
    }
}

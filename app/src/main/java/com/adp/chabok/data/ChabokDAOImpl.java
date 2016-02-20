package com.adp.chabok.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adp.chabok.data.models.MessageTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.tajik
 * on 2/6/2016.
 */
public class ChabokDAOImpl extends SQLiteOpenHelper implements ChabokDAO {

    private static ChabokDAOImpl ourInstance;

    private ChabokDAOImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static ChabokDAOImpl getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ChabokDAOImpl(context);
            return ourInstance;
        }
        return ourInstance;
    }

    @Override
    public void updateCounter(String serverId) {

        int counter = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor myCursor = db.query(TABLE_NAME_MESSAGE, null, "serverId = ?", new String[]{serverId}, null, null, null, null);
        myCursor.moveToFirst();
        if (myCursor.getCount() > 0) {
            counter = myCursor.getInt(myCursor.getColumnIndex("counter"));
            counter++;
            ContentValues cv = new ContentValues();
            cv.put("counter", counter);
            db.update(TABLE_NAME_MESSAGE, cv, "serverId = ?", new String[]{serverId});

        }
        myCursor.close();
        db.close();
    }


    @Override
    public MessageTO saveMessage(MessageTO messageTO, int type) {

        ContentValues initialValues = new ContentValues();

        initialValues.put("serverId", messageTO.getServerId());
        initialValues.put("message", messageTO.getMessage());
        initialValues.put("sentDate", messageTO.getSentDate().getTime());
        initialValues.put("receivedDate", messageTO.getReceivedDate().getTime());
        initialValues.put("read", messageTO.isRead());
        initialValues.put("data", messageTO.getData());
        initialValues.put("type", type);
        initialValues.put("send_status", messageTO.getSendStatus());
        initialValues.put("counter", 0);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME_MESSAGE, null, initialValues);

        db.close();

        messageTO.setId(id);
        return messageTO;
    }

    @Override
    public void deleteAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_MESSAGE, null, null);
        db.close();
    }

    @Override
    public void deleteMessages(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_MESSAGE, " serverId='" + id + "'", null);
        db.close();
    }

    @Override
    public List<MessageTO> getMessages() {
        List<MessageTO> messageTOList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{
                "id",
                "serverId",
                "message",
                "sentDate",
                "receivedDate",
                "read",
                "data",
                "senderId",
                "send_status",
                "counter"
        };

        Cursor results = db.query(TABLE_NAME_MESSAGE, columns, null, null, null, null, null, null);

        if (results.moveToFirst()) {
            for (; !results.isAfterLast(); results.moveToNext()) {
                long id = results.getLong(0);
                String serverId = results.getString(1);
                String message = results.getString(2);
                Timestamp sentDate = new Timestamp(results.getLong(3));
                Timestamp receivedDate = new Timestamp(results.getLong(4));
                boolean read = results.getInt(5) != 0;
                String senderId = results.getString(6);
                int sendStatus = results.getInt(7);
                int seenCounter = results.getInt(8);
                MessageTO messageTO = new MessageTO();
                messageTO.setId(id);
                messageTO.setServerId(serverId);
                messageTO.setMessage(message);
                messageTO.setSentDate(sentDate);
                messageTO.setReceivedDate(receivedDate);
                messageTO.setRead(read);
                messageTO.setSenderId(senderId);
                messageTO.setSendStatus(sendStatus);
                messageTO.setSeenCounter(seenCounter);
                messageTOList.add(messageTO);
            }
        }

        results.close();
        db.close();

        return messageTOList;
    }

    @Override
    public List<MessageTO> getMessages(String orderByColumn) {
        List<MessageTO> messageTOList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{
                "id",
                "serverId",
                "message",
                "sentDate",
                "receivedDate",
                "read",
                "data",
                "send_status",
                "counter"

        };

        Cursor results = db.query(TABLE_NAME_MESSAGE, columns, null, null, null, null, orderByColumn, null);

        if (results.moveToFirst()) {
            for (; !results.isAfterLast(); results.moveToNext()) {
                long id = results.getLong(0);
                String serverId = results.getString(1);
                String message = results.getString(2);
                Timestamp sentDate = new Timestamp(results.getLong(3));
                Timestamp receivedDate = new Timestamp(results.getLong(4));
                boolean read = results.getInt(5) != 0;
                String data = results.getString(6);
                int sendStatus = results.getInt(7);
                int seenCounter = results.getInt(8);

                MessageTO messageTO = new MessageTO();
                messageTO.setId(id);
                messageTO.setServerId(serverId);
                messageTO.setMessage(message);
                messageTO.setSentDate(sentDate);
                messageTO.setReceivedDate(receivedDate);
                messageTO.setRead(read);
                messageTO.setSendStatus(sendStatus);
                messageTO.setData(data);
                messageTO.setSeenCounter(seenCounter);
                if ((data == null) || (data.equals(""))) {

                    messageTO.setData(data);
                }
                messageTOList.add(messageTO);


            }
        }

        results.close();
        db.close();

        return messageTOList;
    }

    @Override
    public void updateSendStatus(String serverId) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("send_status", 1);
        db.update(TABLE_NAME_MESSAGE, cv, "serverId=?", new String[]{serverId});
        db.close();
    }

    @Override
    public int getNormalUnreadedMessagesCount() {

        int result = 0;
        List<MessageTO> messageTOList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{
                "read",
                "data"
        };

        Cursor results = db.query(TABLE_NAME_MESSAGE, columns, null, null, null, null, "receivedDate DESC", null);

        if (results.moveToFirst()) {
            for (; !results.isAfterLast(); results.moveToNext()) {
                boolean read = results.getInt(0) != 0;
                String data = results.getString(1);

                if (data == null) {

                    if (!read) {  // just get unreaded messages

                        result++;
                    }
                }
            }
        }

        results.close();
        db.close();

        return result;
    }


    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(ALTER_TABLE_MESSAGE);

    }
}

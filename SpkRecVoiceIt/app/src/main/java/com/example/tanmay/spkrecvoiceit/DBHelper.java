package com.example.tanmay.spkrecvoiceit;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context){
        super(context, "user_permissions_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("CREATE TABLE IF NOT EXISTS users (userId varchar primary key, user_name varchar unique)");
        db.execSQL("DROP TABLE IF EXISTS users_permissions");
        db.execSQL("CREATE TABLE IF NOT EXISTS users_permissions (userId varchar, pId integer, FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE,FOREIGN KEY(pId) REFERENCES permissions(pId) ON DELETE CASCADE)");
        db.execSQL("DROP TABLE IF EXISTS permissions");
        db.execSQL("CREATE TABLE IF NOT EXISTS permissions (pId integer primary key, permission_name varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS permissions");
        onCreate(sqLiteDatabase);
    }

    public boolean addUser(String userId, String user_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO users(userId, user_name) VALUES ('"+userId+"','"+user_name+"')");
        return true;
    }

    public boolean addPermissions(int pId, String permission_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO permissions(pId, permission_name) VALUES ('"+pId+"','"+permission_name+"')");
        return true;
    }
    public Cursor getUserData(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users where userId ='"+userId+"'", null );
        return res;
    }

    public boolean addUsers_Permissions(String userId, int pId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO users_permissions(userId,pId) VALUES ('"+userId+"','"+pId+"')");
        return true;
    }
    public Cursor getPermissionData(int pId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from permissions where pId= "+pId+"", null );
        return res;
    }
    public Cursor getPermissionsForUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users_permissions where userId="+userId+"", null );
        return res;
    }

    public Cursor getUsers_PermissionsData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users_permissions", null );
        return res;
    }

    public Cursor getAllUsersData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users", null );
        return res;
    }

    public void deleteUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from users where userId="+userId+"");
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from users");
    }
    public void deleteAllPermissions() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from permissions");
    }

    public void deleteAllUserPermissions() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from users_permissions");
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from users");
        db.execSQL("delete from users_permissions");
        db.execSQL("delete from permissions");
    }

}
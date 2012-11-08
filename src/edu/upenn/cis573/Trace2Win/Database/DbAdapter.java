package edu.upenn.cis573.Trace2Win.Database;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.util.Log;

public class DbAdapter {
	
    public static final String CHAR_ROWID = "_id";
    public static final String WORDS_ROWID = "_id";
    public static final String LESSONS_ROWID = "_id";
    public static final String LESSONTAG_ROWID = "_id";
    
    public static final String CHARTAG_ROWID = "_id";
    public static final String CHARTAG_TAG= "tag";
    
    public static final String WORDTAG_ROWID = "_id";
    public static final String WORDTAG_TAG= "tag";

    private static final String TAG = "TagsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_CHAR =
    		"CREATE TABLE Character (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"name TEXT, " +
    		"sort DOUBLE);";
    
    private static final String DATABASE_CREATE_CHARTAG =
            "CREATE TABLE CharacterTag (_id INTEGER, " +
            "tag TEXT NOT NULL, " +
            "FOREIGN KEY(_id) REFERENCES Character(_id));";

    private static final String DATABASE_CREATE_CHAR_DETAILS =
            "CREATE TABLE CharacterDetails (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "CharId INTEGER, " +
            "Stroke INTEGER NOT NULL, " +
            "PointX DOUBLE NOT NULL, " +
            "PointY DOUBLE NOT NULL," +
            "OrderPoint INTEGER NOT NULL, " +
            "FOREIGN KEY(CharId) REFERENCES Character(_id));";
    
    private static final String DATABASE_CREATE_WORDS = 
    		"CREATE TABLE Words (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"name TEXT, " +
    		"sort DOUBLE);";
    
    private static final String DATABASE_CREATE_WORDS_DETAILS =
            "CREATE TABLE WordsDetails (_id INTEGER," +
            "CharId INTEGER," +
            "WordOrder INTEGER NOT NULL," +
            "FlagUserCreated INTEGER," +
            "FOREIGN KEY(CharId) REFERENCES Character(_id)," +
            "FOREIGN KEY(_id) REFERENCES Words(_id));";
    
    private static final String DATABASE_CREATE_WORDSTAG =
            "CREATE TABLE WordsTag (_id INTEGER, " +
            "tag TEXT NOT NULL, " +
            "FOREIGN KEY(_id) REFERENCES Words(_id));";

    private static final String DATABASE_CREATE_LESSONS=
            "CREATE TABLE Lessons (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "name TEXT, " +
            "sort DOUBLE);";
        
    private static final String DATABASE_CREATE_LESSONS_DETAILS =
            "CREATE TABLE LessonsDetails (" +
            "LessonId INTEGER, " +
            "WordId INTEGER," +
            "LessonOrder INTEGER NOT NULL, " +
            "FOREIGN KEY(LessonId) REFERENCES Lessons(_id)," +
            "FOREIGN KEY(WordId) REFERENCES Words(_id));";
    
    private static final String DATABASE_CREATE_LESSONTAG =
            "CREATE TABLE LessonTag (_id INTEGER, " +
            "tag TEXT NOT NULL, " +
            "FOREIGN KEY(_id) REFERENCES Lessons(_id));";
    
    //DB Drop Statements
    
    private static final String DATABASE_DROP_CHAR = 
    		"DROP TABLE IF EXISTS Character";
    private static final String DATABASE_DROP_CHARTAG = 
    		"DROP TABLE IF EXISTS CharacterTag";
    private static final String DATABASE_DROP_CHAR_DETAILS = 
    		"DROP TABLE IF EXISTS CharacterDetails";
    private static final String DATABASE_DROP_WORDS = 
    		"DROP TABLE IF EXISTS Words";
    private static final String DATABASE_DROP_WORDS_DETAILS = 
    		"DROP TABLE IF EXISTS WordsDetails";
    private static final String DATABASE_DROP_WORDSTAG = 
    		"DROP TABLE IF EXISTS WordsTag";
    private static final String DATABASE_DROP_LESSONS = 
    		"DROP TABLE IF EXISTS Lessons";
    private static final String DATABASE_DROP_LESSONS_DETAILS = 
    		"DROP TABLE IF EXISTS LessonsDetails";
    private static final String DATABASE_DROP_LESSONTAG= 
    		"DROP TABLE IF EXISTS LessonTag";
    
    
    
    
    
    private static final String DATABASE_NAME = "CharTags";
    private static final String CHAR_TABLE = "Character";
    private static final String CHAR_DETAILS_TABLE = "CharacterDetails";
    private static final String CHARTAG_TABLE = "CharacterTag";
    private static final String WORDTAG_TABLE = "WordsTag";
    private static final String WORDS_TABLE = "Words";
    private static final String WORDS_DETAILS_TABLE = "WordsDetails";
    private static final String LESSONS_TABLE = "Lessons";
    private static final String LESSONS_DETAILS_TABLE = "LessonsDetails";
    private static final String LESSONTAG_TABLE = "LessonTag";
    
    
    private static final int DATABASE_VERSION = 6;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_CHAR);
            db.execSQL(DATABASE_CREATE_CHARTAG);
            db.execSQL(DATABASE_CREATE_CHAR_DETAILS);
            db.execSQL(DATABASE_CREATE_WORDS);
            db.execSQL(DATABASE_CREATE_WORDS_DETAILS);
            db.execSQL(DATABASE_CREATE_WORDSTAG);
            db.execSQL(DATABASE_CREATE_LESSONS);
            db.execSQL(DATABASE_CREATE_LESSONS_DETAILS);
            db.execSQL(DATABASE_CREATE_LESSONTAG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            Log.w(TAG, "Upgrading database from version " + oldVer + " to "
                    + newVer + ", which will destroy all old data");
            db.execSQL(DATABASE_DROP_CHAR);
            db.execSQL(DATABASE_DROP_CHARTAG);
            db.execSQL(DATABASE_DROP_CHAR_DETAILS);
            db.execSQL(DATABASE_DROP_WORDS);
            db.execSQL(DATABASE_DROP_WORDS_DETAILS);
            db.execSQL(DATABASE_DROP_WORDSTAG);
            db.execSQL(DATABASE_DROP_LESSONS);
            db.execSQL(DATABASE_DROP_LESSONS_DETAILS);
            db.execSQL(DATABASE_DROP_LESSONTAG);     
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the CharTags database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new character tag. If the character tag is
     * successfully created return the new rowId for that tag, otherwise return
     * a -1 to indicate failure.
     * 
     * @param id the row_id of the tag
     * @param tag the text of the tag
     * @return rowId or -1 if failed
     */
    public long createWordTags(long id, String tag) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(WORDTAG_ROWID, id);
        initialValues.put(WORDTAG_TAG, tag);

        return mDb.insert(WORDTAG_TABLE, null, initialValues);
    }
    
    /**
     * Create a new lesson tag. If the lesson tag is
     * successfully created return the new rowId for that tag, otherwise sreturn
     * a -1 to indicate failure.
     * 
     * @param id the row_id of the tag
     * @param tag the text of the tag
     * @return rowId or -1 if failed
     */
    public long createLessonTags(long id, String tag) {
    	ContentValues initialValues = new ContentValues();
        initialValues.put("_id", id);
        initialValues.put("tag", tag);

        return mDb.insert(LESSONTAG_TABLE, null, initialValues);
    }
    
    /**
     * Create a new word tag. If the tag is
     * successfully created return the new rowId for that tag, otherwise return
     * a -1 to indicate failure.
     * 
     * @param id the row_id of the tag
     * @param tag the text of the tag
     * @return rowId or -1 if failed
     */
    public long createTags(long id, String tag) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHARTAG_ROWID, id);
        initialValues.put(CHARTAG_TAG, tag);

        return mDb.insert(CHARTAG_TABLE, null, initialValues);
    }
    
    /**
     * Delete the tag with the given rowId and tag
     * 
     * @param rowId id of tag to delete
     * @param tag text of tag to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTag(long rowId, String tag) {
        return mDb.delete(CHARTAG_TABLE, CHARTAG_ROWID + "=" + rowId + " AND " + CHARTAG_TAG+"="+tag, null) > 0;
    }
    
    /**
     * Delete the word tag with the given rowId and tag
     * 
     * @param rowId id of tag to delete
     * @param tag text of tag to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteWordTag(long rowId, String tag) {
        return mDb.delete(WORDTAG_TABLE, WORDTAG_ROWID + "=" + rowId + " AND " + WORDTAG_TAG+"="+tag, null) > 0;
    }
   
    /**
     * Modify a character already in the database
     * @param c character to be modified to the database
     * @return true if change is pushed to DB.  False on error.
     */
    public boolean modifyCharacter(LessonCharacter c)
    {
    	mDb.beginTransaction();
    	long charId = c.getId();
    	//drop the current details
    	mDb.delete(CHAR_DETAILS_TABLE, "CharId = " + charId, null);
    	
    	//add each stroke to CHAR_DETAILS_TABLE
    	List<Stroke> l = c.getStrokes();
    	//stroke ordering
    	int strokeNumber=0;
    	for(Stroke s:l)
    	{
    		ContentValues strokeValues = new ContentValues();
    		strokeValues.put("CharId", charId);
    		strokeValues.put("Stroke", strokeNumber);
    		//point ordering
    		int pointNumber=0;
    		for(PointF p : s.getSamplePoints())
    		{
    			strokeValues.put("PointX", p.x);
        		strokeValues.put("PointY", p.y);
        		strokeValues.put("OrderPoint", pointNumber);
        		long success = mDb.insert(CHAR_DETAILS_TABLE, null, strokeValues);
        		if(success == -1)
        		{	
        			//if error
        			Log.e(CHAR_DETAILS_TABLE,"cannot add stroke");
        			mDb.endTransaction();
        			return false;
        		}
        		pointNumber++;
    		}
    		strokeNumber++;
    	}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    	
    }
    
    
    
    /**
     * Add a character to the database
     * @param c character to be added to the database
     * @return true if character is added to DB.  False on error.
     */
    public boolean addCharacter(LessonCharacter c)
    {
    	mDb.beginTransaction();
    	//add to CHAR_TABLE
    	ContentValues initialCharValues = new ContentValues();
    	initializePrivateTag(c, initialCharValues);
    	long id = mDb.insert(CHAR_TABLE, null, initialCharValues);
    	if(id == -1)
    	{
    		//if error
    		Log.e(CHAR_TABLE, "cannot add new character to table " + CHAR_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	Cursor x = mDb.query(CHAR_TABLE, new String[]{CHAR_ROWID}, null, null, null, null, CHAR_ROWID+" DESC", "1");
    	if (x != null) {
            x.moveToFirst();
        }
    	c.setId(x.getInt(x.getColumnIndexOrThrow(CHAR_ROWID))); // TODO RLi: is this not equal to the local variable id returned by mDb.insert?
    	x.close();
    	
    	// if the given character has tags, copy them
    	for (String tag : c.getTags()) {
    		if (-1 == createTags(id, tag)) {
        		Log.e(CHAR_TABLE, "cannot add character's tag(" + tag + ") to table " + CHAR_TABLE);
    			mDb.endTransaction();
        		return false;
    		}
    	}
    	
    	// To make the sort order the same as the ID, we need to update the row
    	// after we get the ID, i.e. now.
    	c.setSort(c.getId()); // sort value initialized to ID.
        initialCharValues.put("sort", c.getSort());
        mDb.update(CHAR_TABLE, initialCharValues, CHAR_ROWID + "=" + id, null);
    	
    	//add each stroke to CHAR_DETAILS_TABLE
    	List<Stroke> l = c.getStrokes();
    	//stroke ordering
    	int strokeNumber=0;
    	for(Stroke s : l)
    	{
    		ContentValues strokeValues = new ContentValues();
    		strokeValues.put("CharId", id);
    		strokeValues.put("Stroke", strokeNumber);
    		//point ordering
    		int pointNumber=0;
    		for(PointF p : s.getSamplePoints())
    		{
    			strokeValues.put("PointX", p.x);
        		strokeValues.put("PointY", p.y);
        		strokeValues.put("OrderPoint", pointNumber);
        		long success = mDb.insert(CHAR_DETAILS_TABLE, null, strokeValues);
        		if(success == -1)
        		{	
        			//if error
        			Log.e(CHAR_DETAILS_TABLE,"cannot add stroke");
        			mDb.endTransaction();
        			return false;
        		}
        		pointNumber++;
    		}
    		strokeNumber++;
    	}
    	//need to add character as a word so that we can add them to lessons as not part of a word
    	ContentValues initialWordValue = new ContentValues();
    	initialWordValue.put("name", "");
    	long word_id = mDb.insert(WORDS_TABLE, null, initialWordValue);
    	if(word_id == -1)
    	{
    		//if error
    		Log.e(WORDS_TABLE, "cannot add new character to table " + 
    		        WORDS_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	
        // To make the sort order the same as the ID, we need to update the row
        // after we get the ID, i.e. now.
        initialWordValue.put("sort", word_id);
        mDb.update(WORDS_TABLE, initialWordValue, WORDS_ROWID + "=" + word_id,
                null);
    	
        // Add word details
        Cursor cur = mDb.query(WORDS_TABLE, new String[]{"_id"}, null, null, null, null, "_id DESC", "1");
    	if (cur != null) {
            cur.moveToFirst();
        }
    	word_id = cur.getInt(cur.getColumnIndexOrThrow("_id"));
    	cur.close();
    	ContentValues wordValues = new ContentValues();
    	wordValues.put("_id", word_id);
    	wordValues.put("CharId", id);
    	wordValues.put("WordOrder", 0);
    	wordValues.put("FlagUserCreated", 0);
    	long success = mDb.insert(WORDS_DETAILS_TABLE, null, wordValues);
		if(success == -1)
		{	
			//if error
			Log.e(WORDS_DETAILS_TABLE,"cannot add to table");
			mDb.endTransaction();
			return false;
		}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    	
    }
    
    public long deleteCharacter(long id){
    	Cursor mCursor =
                mDb.query(true, CHAR_TABLE, new String[] {CHAR_ROWID}, CHAR_ROWID + "=" + id, null,
                        null, null, null, null);
    	 if (mCursor == null) {
             return -2;
         }
         mCursor.close();
    	 
    	 mCursor = mDb.query(true, WORDS_DETAILS_TABLE, new String[] {"CharId"}, "CharId =" + id +" AND FlagUserCreated=1", null,
                 null, null, null, null);
    	 if(mCursor.getCount()>0){
    		 //Some word is using the character
    	     mCursor.close();
    		 return -1;
    	 }
    	 else{
    	     mCursor.close();

    	     mDb.delete(CHAR_TABLE, CHAR_ROWID + "=" + id, null);
    		 mDb.delete(CHAR_DETAILS_TABLE, "CharId = " + id, null);
    		 mCursor =  mDb.query(true, WORDS_DETAILS_TABLE, new String[] {WORDS_ROWID}, "CharId =" + id, null,
                     null, null, null, null);
    		 mCursor.moveToFirst();
    		 do {
 	        	if(mCursor.getCount()==0){
 	        		break;
 	        	}
 	        	long wordId = (mCursor.getLong(mCursor.getColumnIndexOrThrow(WORDS_ROWID)));
 	        	mDb.delete(WORDS_TABLE, WORDS_ROWID + "=" + wordId, null);
 	         }
 	         while(mCursor.moveToNext());
    		 mDb.delete(WORDS_DETAILS_TABLE, "CharId="+id, null);
    		 mDb.delete(CHARTAG_TABLE, CHAR_ROWID + "=" + id, null);
    	 }
    	 mCursor.close();
    	 return id;
    }
    
    
    /**
     * Get a LessonCharacter from the database
     * @param id id of the LessonCharacter
     * @return The LessonCharacter if id exists, null otherwise.
     */
    public LessonCharacter getCharacterById(long id)
    {
    	Cursor mCursor =
    			mDb.query(true, CHAR_TABLE, new String[] {CHAR_ROWID}, CHAR_ROWID + "=" + id, null,
    					null, null, null, null);
    	LessonCharacter c = new LessonCharacter();
    	//if the character doesn't exists
    	if (mCursor == null) {
    		return null;
    	}
    	mCursor.close();

    	//grab its details (step one might not be necessary and might cause slow downs
    	// but it is for data consistency.
    	mCursor =
    			mDb.query(true, CHAR_DETAILS_TABLE, new String[] {"CharId", "Stroke","PointX","PointY"}, "CharId = "+ id, null,
    					null, null, "Stroke ASC, OrderPoint ASC", null);
    	mCursor.moveToFirst();
    	Stroke s = new Stroke();
    	if (mCursor.getCount() > 0) {
    		int strokeNumber = mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke"));

    		do {
    			if(mCursor.getCount()==0){
    				c.addStroke(s);
    				break;
    			}
    			if(strokeNumber != mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke")))
    			{
    				c.addStroke(s);
    				strokeNumber = mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke"));
    				s = new Stroke();
    			}
    			s.addPoint(mCursor.getFloat(mCursor.getColumnIndexOrThrow("PointX")),
    					mCursor.getFloat(mCursor.getColumnIndexOrThrow("PointY")));
    		}
    		while(mCursor.moveToNext());
    		c.addStroke(s);
    	}
    	c.setId(id);
    	mCursor.close();

    	mCursor =
    			mDb.query(true, CHAR_TABLE, new String[] {"name", "sort"},
    					CHAR_ROWID + " = "+ id, null, null, null, null, null);
    	mCursor.moveToFirst();
    	String privateTag = mCursor.getString(mCursor.getColumnIndexOrThrow("name"));
    	c.setPrivateTag(privateTag);
    	double sort = mCursor.getDouble(mCursor.getColumnIndexOrThrow("sort"));
    	c.setSort(sort);
    	mCursor.close();

    	// get tags as well
    	for (String tag : getCharacterTags(id)) {
    		c.addTag(tag);
    	}

    	return c;
    }
    


    /**
     * Get a LessonCharacter from the database
     * @param id id of the LessonCharacter
     * @return The LessonCharacter if id exists, null otherwise.
     */
    public LessonWord getWordById(long id)
    {
        Cursor mCursor =
            mDb.query(true, WORDS_TABLE, new String[] {WORDS_ROWID}, WORDS_ROWID + "=" + id, null,
                    null, null, null, null);
        LessonWord w = new LessonWord();
        //if the character doesn't exists
        if (mCursor == null) {
            return null;
        }
        mCursor.close();
        
        //grab its details (step one might not be necessary and might cause slow downs
        // but it is for data consistency.
        mCursor =
            mDb.query(true, WORDS_DETAILS_TABLE, new String[] {WORDS_ROWID, "CharId", "WordOrder"}, WORDS_ROWID + "=" + id, null,
                    null, null, "WordOrder ASC", null);
        mCursor.moveToFirst();
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	long charId = mCursor.getLong(mCursor.getColumnIndexOrThrow("CharId"));
        	Log.i("LOAD", "Char: " + charId);
        	w.addCharacter(charId);
        } while(mCursor.moveToNext());
        w.setId(id);
        mCursor.close();
        
        mCursor =
                mDb.query(true, WORDS_TABLE, new String[] {"name","sort"},
                        WORDS_ROWID + " = " + id, null, null, null, null, null);
        mCursor.moveToFirst();
        String privateTag = mCursor.getString(mCursor.getColumnIndexOrThrow("name"));
        w.setPrivateTag(privateTag);
        double sort = mCursor.getDouble(mCursor.getColumnIndexOrThrow("sort"));
        w.setSort(sort);
        
        w.setDatabase(this);

        mCursor.close();

        return w;
    }
     
    /**
     * Add a word to the database
     * @param w word to be added to the database
     * @return true if word is added to DB.  False on error.
     */
    public boolean addWord(LessonWord w)
    {
    	mDb.beginTransaction();
    	//add to WORDS_TABLE
    	ContentValues initialWordsValues = new ContentValues();
    	initializePrivateTag(w, initialWordsValues);
    	long id = mDb.insert(WORDS_TABLE, null, initialWordsValues);
    	if(id == -1)
    	{
    		//if error
    		Log.e(WORDS_TABLE, "cannot add new character to table "+WORDS_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	Cursor x = mDb.query(WORDS_TABLE, new String[]{"_id"}, null, null, null, null, "_id DESC", "1");
    	if (x != null) {
            x.moveToFirst();
        }
    	w.setId(x.getInt(x.getColumnIndexOrThrow("_id")));
        x.close();
    	
        // To make the sort order the same as the ID, we need to update the row
        // after we get the ID, i.e. now.
        w.setSort(w.getId()); // sort value initialized to ID.
        initialWordsValues.put("sort", w.getSort());
        mDb.update(WORDS_TABLE, initialWordsValues, WORDS_ROWID + "=" + id, null);
    	
    	//add each character to WORDS_DETAILS_TABLE
    	List<Long> l = w.getCharacterIds();
    	//character ordering
    	int charNumber=0;
    	for(Long c:l)
    	{
    		ContentValues characterValues = new ContentValues();
    		characterValues.put("_id", id);
    		characterValues.put("CharId", c.intValue());
    		characterValues.put("WordOrder", charNumber);
    		characterValues.put("FlagUserCreated", 1);
    		long success = mDb.insert(WORDS_DETAILS_TABLE, null, characterValues);
    		if(success == -1)
    		{	
    			//if error
    			Log.e(WORDS_DETAILS_TABLE,"cannot add to table");
    			mDb.endTransaction();
    			return false;
    		}
    		charNumber++;
    	}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    	
    }
    
    public long deleteWord(long id){
    	Cursor mCursor =
                mDb.query(true, WORDS_TABLE, new String[] {WORDS_ROWID}, WORDS_ROWID + "=" + id, null,
                        null, null, null, null);
    	 if (mCursor == null) {
             return -1;
         }
    	 
		 mDb.delete(WORDS_TABLE, WORDS_ROWID + "=" + id, null);
		 mDb.delete(WORDS_DETAILS_TABLE, "_id = " + id, null);
		 mDb.delete(WORDTAG_TABLE, "_id="+id, null);
		 mDb.delete(LESSONS_DETAILS_TABLE, "WordId="+id, null);
    	 
		 mCursor.close();

		 return id;
    }
    
    public String getPrivateTag(long id, ItemType type)
    {
    	String tableName;
    	switch(type)
    	{
    	case CHARACTER:
    		tableName=CHAR_TABLE;
    		break;
    	case WORD:
    		tableName=WORDS_TABLE;
    		break;
    	case LESSON:
    		tableName=LESSONS_TABLE;
    		break;
    	default:
    		Log.e("Tag", "Unsupported Type");
    		return "";
    	}
    	Cursor mCursor =
    			mDb.query(true, tableName, new String[] {"_id","name"}, "_id=" + id, null,
    					null, null, null, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
        	String ret = mCursor.getString(mCursor.getColumnIndexOrThrow("name"));
            mCursor.close();
            return ret;
    	}

    	return "";
    }
    
    /**
     * Return a List of tags that matches the given character's charId
     * 
     * @param charId id of character whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if character could not be found/retrieved
     */
    public List<String> getCharacterTags(long charId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, CHARTAG_TABLE, new String[] {CHARTAG_TAG}, CHARTAG_ROWID + "=" + charId, null,
                    null, null, CHARTAG_TAG+" ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow(CHARTAG_TAG)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }
    
    /**
     * Return a Cursor positioned at the character that matches the partial tag
     * if the tag is more than 2 chars, or the entire tag for 1 or 2 chars.
     * 
     * @param tag text of tag to match
     * @return Cursor positioned to matching character, if found
     * @throws SQLException if character could not be found/retrieved
     */
    public Cursor getChars(String tag) throws SQLException {

        Cursor mCursor;
        if(tag.length() < 2){
        	mCursor = mDb.query(true, CHARTAG_TABLE, 
        	        new String[] {CHARTAG_ROWID},
        	        CHARTAG_TAG + " LIKE '" + tag + "'", 
        	        null, null, null, CHARTAG_ROWID + " ASC", null);
        }
        else{
        	mCursor = mDb.query(true, CHARTAG_TABLE, 
        	        new String[] {CHARTAG_ROWID}, 
        	        CHARTAG_TAG + " LIKE '%" + tag + "%'", 
        	        null, null, null, CHARTAG_ROWID + " ASC", null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Returns a cursor with all chars that partially match tag
     * @param tag a partial tag
     * @return a Cursor
     * @throws SQLException
     */
    public Cursor getAllChars(String tag) throws SQLException {
        Cursor mCursor;
        mCursor = mDb.query(true, CHARTAG_TABLE, 
                new String[] {CHARTAG_ROWID}, 
                CHARTAG_TAG + " LIKE '" + tag + "%'", 
                null, null, null, CHARTAG_ROWID + " ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
   
    /**
     * Return a List of tags that matches the given Lesson's id
     * 
     * @param lessonId id of lesson whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if lesson could not be found/retrieved
     */
    public List<String> getLessonTags(long lessonId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, LESSONTAG_TABLE, new String[] {"tag"}, "_id" + "=" + lessonId, null,
                    null, null, "tag"+" ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow("tag")));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }
    
    /**
     * Return a List of tags that matches the given word's wordId
     * 
     * @param wordId id of word whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if word could not be found/retrieved
     */
    public List<String> getWordTags(long wordId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, WORDTAG_TABLE, new String[] {WORDTAG_TAG}, WORDTAG_ROWID + "=" + wordId, null,
                    null, null, WORDTAG_TAG+" ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow(WORDTAG_TAG)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }
    
    /**
     * Return a Cursor positioned at the word that matches the given tag
     * 
     * @param tag text of tag to match
     * @return Cursor positioned to matching word, if found
     * @throws SQLException if word could not be found/retrieved
     */
    public Cursor getWords(String tag) throws SQLException {

        Cursor mCursor =
            mDb.query(true, WORDTAG_TABLE, new String[] {WORDTAG_ROWID}, WORDTAG_TAG + "='" + tag+"'", null,
                    null, null, WORDTAG_ROWID + " ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a list of char ids from the database
     * @return ids list of all char ids
     */
    public List<Long> getAllCharIds(){
    	 Cursor mCursor =
	            mDb.query(true, CHAR_TABLE, new String[] {CHAR_ROWID}, null, null,
	                    null, null, CHAR_ROWID+" ASC", null);
    	 List<Long> ids = new ArrayList<Long>();
    	 if (mCursor != null) {
    	     mCursor.moveToFirst();
    	 } else {
    	     return null;
    	 }
    	 do {
    	     if(mCursor.getCount()==0){
    	         break;
    	     }
    	     ids.add(mCursor.getLong(mCursor.getColumnIndexOrThrow(CHAR_ROWID)));
    	 }
    	 while(mCursor.moveToNext());
         mCursor.close();

    	 return ids;
    }
    
    /**
     * Return a Cursor positioned at all characters
     * @return Cursor positioned to characters
     */
    public Cursor getAllCharIdsCursor(){
        Cursor mCursor =
                mDb.query(true, CHAR_TABLE, new String[] {CHAR_ROWID}, null, null,
                        null, null, CHAR_ROWID+" ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Updates a private tag for a character. 
     * 
     * @param id row id for a character
     * @param tag the text of the tag to add
     * @return number of rows that were affected, 0 on no rows affected
     */
    public long updatePrivateTag(long id, String tag){
    	ContentValues initialValues = new ContentValues();
        initialValues.put(CHAR_ROWID, id);
        initialValues.put("name", tag);
        Log.e("Adding Private Tag",tag);
        return mDb.update(CHAR_TABLE, initialValues, CHAR_ROWID + "=" + id, 
                null);
    }
    
    /**
     * Updates a private tag for a word. Returns row id on 
     * 
     * @param id row id for a word
     * @param tag the text of the tag to add
     * @return number of rows that were affected, 0 on no rows affected
     */
    public long updatePrivateWordTag(long id, String tag){
    	ContentValues initialValues = new ContentValues();
        //initialValues.put(CHAR_ROWID, id);
        initialValues.put("name", tag);

        return mDb.update(WORDS_TABLE, initialValues, "_id="+id,null);
    }
    
    /**
     * Return a list of word ids from the database
     * @return ids list of all word ids
     */
    public List<Long> getAllWordIds() {
        Cursor mCursor =
                mDb.query(true, WORDS_TABLE, new String[] {WORDS_ROWID}, null, null,
                        null, null, WORDS_ROWID+" ASC", null);
        List<Long> ids = new ArrayList<Long>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            ids.add(mCursor.getLong(mCursor.getColumnIndexOrThrow(WORDS_ROWID)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return ids;
    }
    
    public List<String> getAllLessonNames(){
        Cursor mCursor =
                mDb.query(true, LESSONS_TABLE, new String[] {"name"}, null, null,
                        null, null, "name ASC", null);
        List<String> names = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            names.add(mCursor.getString((mCursor.getColumnIndexOrThrow("name"))));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return names;
    }
    
    public long addWordToLesson(String lessonName, long wordId){
    	mDb.beginTransaction();
    	Cursor x = mDb.query(LESSONS_TABLE, new String[]{"_id"}, "name='"+lessonName+"'", null, null, null, null, null);
    	if (x != null) {
            x.moveToFirst();
        }
    	else{
    		return -1;
    	}
    	int lessonId = x.getInt(x.getColumnIndexOrThrow("_id"));
        x.close();

    	x = mDb.query(LESSONS_DETAILS_TABLE, new String[]{"LessonOrder"}, null, null, null, null, "LessonOrder DESC", "1");
    	if (x != null) {
            x.moveToFirst();
        }
    	else{
    		return -1;
    	}
    	int lessonOrder = x.getInt(x.getColumnIndexOrThrow("LessonOrder"));
        x.close();

    	ContentValues values = new ContentValues();
    	values.put("LessonId", lessonId);
    	values.put("WordId", wordId);
    	values.put("LessonOrder",lessonOrder);
    	long ret = mDb.insert(LESSONS_DETAILS_TABLE, null, values);
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return ret;
    }
    
    /**
     * Add a Lesson to the database
     * @param les lesson to be added to the database
     * @return true if lesson is added to DB.  False on error.
     */
    public boolean addLesson(Lesson les)
    {
    	mDb.beginTransaction();
    	//add to WORDS_TABLE
    	ContentValues initialLessonValues = new ContentValues();
    	initializePrivateTag(les,initialLessonValues);
    	long id = mDb.insert(LESSONS_TABLE, null, initialLessonValues);
    	if(id == -1)
    	{
    		//if error
    		Log.e(LESSONS_TABLE, "cannot add new character to table "+LESSONS_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	Cursor x = mDb.query(LESSONS_TABLE, new String[]{"_id"}, null, null, null, null, "_id DESC", "1");
    	if (x != null) {
            x.moveToFirst();
        }
    	les.setId(x.getInt(x.getColumnIndexOrThrow("_id")));
        x.close();

    	//add each word to LESSONS_DETAILS_TABLE
    	List<Long> l = les.getWordIds();
    	//word ordering
    	int wordNumber=0;
    	for(Long wordId:l)
    	{
    		ContentValues lessonValues = new ContentValues();
    		lessonValues.put("LessonId", id);
    		lessonValues.put("WordId", wordId);
    		lessonValues.put("LessonOrder", wordNumber);
    		long success = mDb.insert(LESSONS_DETAILS_TABLE, null, lessonValues);
    		if(success == -1)
    		{	
    			//if error
    			Log.e(LESSONS_DETAILS_TABLE,"cannot add to table");
    			mDb.endTransaction();
    			return false;
    		}
    		wordNumber++;
    	}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    }
    
    public List<Long> getWordsFromLessonId(long id){
    	Cursor mCursor =
    			mDb.query(true, LESSONS_DETAILS_TABLE, new String[] {"WordId"}, "LessonId="+id, null,
    					null, null, "LessonOrder ASC", null);
    	List<Long> ids = new ArrayList<Long>();
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	do {
    		if(mCursor.getCount()==0){
    			break;
    		}
    		ids.add(mCursor.getLong(mCursor.getColumnIndexOrThrow("WordId")));
    	}
    	while(mCursor.moveToNext());
        mCursor.close();

    	return ids;
    }
    
    /**
     * Return a list of lesson ids from the database
     * @return ids list of all lesson ids
     */
    public List<Long> getAllLessonIds() {
        Cursor mCursor =
                mDb.query(true, LESSONS_TABLE, new String[] {LESSONS_ROWID}, null, null,
                        null, null, LESSONS_ROWID+" ASC", null);
        List<Long> ids = new ArrayList<Long>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            ids.add(mCursor.getLong(mCursor.getColumnIndexOrThrow(LESSONS_ROWID)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return ids;
    }
    
    /**
     * Deletes the lesson by lesson id
     * @param id 
     * @return id if found, -1 if not
     */
    public long deleteLesson(long id){
        Cursor mCursor =
                mDb.query(true, LESSONS_TABLE, new String[] {LESSONS_ROWID}, LESSONS_ROWID + "=" + id, null,
                        null, null, null, null);
        int rowsDeleted=0;
        if (mCursor == null) {
            return -1;
        }
        else{
            mCursor.close();

            rowsDeleted += mDb.delete(LESSONS_TABLE, LESSONS_ROWID + "=" + id, null);
            rowsDeleted += mDb.delete(LESSONS_DETAILS_TABLE, "LessonId = " + id, null);
            rowsDeleted += mDb.delete(LESSONTAG_TABLE, LESSONTAG_ROWID + "=" + id, null);
        }
        if(rowsDeleted>0)
            return id;
        else
            return -1;

    }
    
    /**
     * @param id
     * @return
     */
    public Lesson getLessonById(long id) {
    	Cursor mCursor =
    			mDb.query(true, LESSONS_TABLE, new String[] {LESSONS_ROWID, "name"}, LESSONS_ROWID + "=" + id, null,
    					null, null, null, null);
    	Lesson le = new Lesson();
    	//if the Lesson doesn't exists
    	if (mCursor == null) {
    		return null;
    	}else{
    		mCursor.moveToFirst();
    		le.setName(mCursor.getString(mCursor.getColumnIndexOrThrow("name")));
    	}
        mCursor.close();

    	//SUSPECT: grab its details (step one might not be necessary and might cause slow downs
    	// but it is for data consistency.
    	mCursor =
    			mDb.query(true, LESSONS_DETAILS_TABLE, new String[] { "LessonId", "WordId", "LessonOrder"}, "LessonId" + "=" + id, null,
    					null, null, "LessonOrder ASC", null);
    	mCursor.moveToFirst();
    	do {
    		if(mCursor.getCount()==0){
    			break;
    		}
    		long wordId = mCursor.getLong(mCursor.getColumnIndexOrThrow("WordId"));
    		Log.i("LOAD", "Word: " + wordId);
    		le.addWord(wordId);
    	} while(mCursor.moveToNext());
        mCursor.close();

    	le.setId(id);
    	le.setDatabase(this);
    	
    	return le;
    }
    
    /**
     * Swap the display order of two characters.
     * @param aId id of first character
     * @param aSort sort value of first character
     * @param bId id of second character
     * @param bSort sort value of second character
     * @return true if the transaction was successful, false otherwise
     */
    public boolean swapCharacters(long aId, double aSort, long bId, double bSort) {
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(CHAR_ROWID, aId);
        bValues.put(CHAR_ROWID, bId);
        aValues.put("sort", bSort);
        bValues.put("sort", aSort);
        Log.e("Swapping positions", aId + " and " + bId);
        
        int result;
        result = mDb.update(CHAR_TABLE, aValues, CHAR_ROWID + "=" + aId, null);
        if (result == -1) {
            Log.e(CHAR_TABLE, "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(CHAR_TABLE, bValues, CHAR_ROWID + "=" + bId, null);
        if (result == -1) {
            Log.e(CHAR_TABLE, "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    
    /**
     * Swap the display order of two words.
     * @param aId id of first word
     * @param aSort sort value of first word
     * @param bId id of second word
     * @param bSort sort value of second word
     * @return true if the transaction was successful, false otherwise
     */
    public boolean swapWords(long aId, double aSort, long bId, double bSort) {
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(WORDS_ROWID, aId);
        bValues.put(WORDS_ROWID, bId);
        aValues.put("sort", bSort);
        bValues.put("sort", aSort);
        Log.e("Swapping positions", aId + " and " + bId);
        
        int result;
        result = mDb.update(WORDS_TABLE, aValues, WORDS_ROWID + "=" + aId, null);
        if (result == -1) {
            Log.e(WORDS_TABLE, "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(WORDS_TABLE, bValues, WORDS_ROWID + "=" + bId, null);
        if (result == -1) {
            Log.e(WORDS_TABLE, "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    
    /**
     * Initializes a private tag
     * 
     * @param i the LessonItem
     * @param v ContentValues
     */
    private void initializePrivateTag(LessonItem i, ContentValues v)
    {
    	if(i.getPrivateTag() != null)
    		v.put("name",i.getPrivateTag());
    	else	
    		v.put("name","");
    }
}

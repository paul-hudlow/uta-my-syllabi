package edu.uta.mysyllabi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

public class MySyllabi extends Application {
	
    private static Context context;
    
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "edu.uta.mysyllabi.backend";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "edu.uta.mysyllabi";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account account;
    ContentResolver mResolver;
    
 // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 10L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
            SECONDS_PER_MINUTE *
            MILLISECONDS_PER_SECOND;

    public void onCreate(){
        super.onCreate();
        MySyllabi.context = getApplicationContext();
        
        //account = CreateSyncAccount(this);
        
        //mResolver = getContentResolver();
        
        //ContentResolver.addPeriodicSync(
        //        account,
        //        AUTHORITY,
        //        null,
        //        SYNC_INTERVAL);
    }
    
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
        	return newAccount;
        } else {
            return null;
        }
        
    }

    public static Context getAppContext() {
        return MySyllabi.context;
    }

}

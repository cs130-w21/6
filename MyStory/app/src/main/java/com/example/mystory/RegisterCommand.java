package com.example.mystory;

import android.content.Context;
import android.os.AsyncTask;

public class RegisterCommand implements Command {
    private String mUserName;
    private String mPassword;
    private Context mContext;
    private CreateRequest mRequestMethod;
    private HandleResponse mResponseMethod;

    public RegisterCommand(Context context,
                           CreateRequest requestMethod,
                           HandleResponse responseMethod,
                           String userName,
                           String password) {
        mContext = context;
        mRequestMethod = requestMethod;
        mResponseMethod = responseMethod;
        mUserName = userName;
        mPassword = password;
    }

    public void myExecute() {
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Integer> {
        private String request;
        private char[] response = new char[100];

        @Override
        protected Integer doInBackground(Void... voids) {
            request = mRequestMethod.createRequest(mUserName, mPassword, null);
            SocketHandler socketHandler = new SocketHandler();
            socketHandler.writeToSocket(request.toCharArray());
            return socketHandler.readFromSocket(response);
        }

        @Override
        protected void onPostExecute(Integer responseLength) {
            super.onPostExecute(responseLength);
            mResponseMethod.handleResponse(mContext, response, responseLength);
        }
    }
}

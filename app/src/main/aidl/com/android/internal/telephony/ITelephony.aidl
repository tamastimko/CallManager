// ITelephony.aidl
package com.android.internal.telephony;

//szükséges a bejövő hívás elutasításához
interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}

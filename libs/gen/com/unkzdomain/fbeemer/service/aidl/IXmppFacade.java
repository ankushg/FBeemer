/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/amgupt01/Desktop/beem/src/com/unkzdomain/fbeemer/service/aidl/IXmppFacade.aidl
 */
package com.unkzdomain.fbeemer.service.aidl;
public interface IXmppFacade extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.unkzdomain.fbeemer.service.aidl.IXmppFacade
{
private static final java.lang.String DESCRIPTOR = "com.unkzdomain.fbeemer.service.aidl.IXmppFacade";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.unkzdomain.fbeemer.service.aidl.IXmppFacade interface,
 * generating a proxy if needed.
 */
public static com.unkzdomain.fbeemer.service.aidl.IXmppFacade asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.unkzdomain.fbeemer.service.aidl.IXmppFacade))) {
return ((com.unkzdomain.fbeemer.service.aidl.IXmppFacade)iin);
}
return new com.unkzdomain.fbeemer.service.aidl.IXmppFacade.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_createConnection:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.aidl.IXmppConnection _result = this.createConnection();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_getRoster:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.aidl.IRoster _result = this.getRoster();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_connectSync:
{
data.enforceInterface(DESCRIPTOR);
this.connectSync();
reply.writeNoException();
return true;
}
case TRANSACTION_connectAsync:
{
data.enforceInterface(DESCRIPTOR);
this.connectAsync();
reply.writeNoException();
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(DESCRIPTOR);
this.disconnect();
reply.writeNoException();
return true;
}
case TRANSACTION_getChatManager:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.aidl.IChatManager _result = this.getChatManager();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_changeStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.changeStatus(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_sendPresencePacket:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.PresenceAdapter _arg0;
if ((0!=data.readInt())) {
_arg0 = com.unkzdomain.fbeemer.service.PresenceAdapter.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.sendPresencePacket(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_call:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.call(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getVcardAvatar:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
byte[] _result = this.getVcardAvatar(_arg0);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.unkzdomain.fbeemer.service.aidl.IXmppFacade
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Get the XmppConnection of the facade.
     */
public com.unkzdomain.fbeemer.service.aidl.IXmppConnection createConnection() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.unkzdomain.fbeemer.service.aidl.IXmppConnection _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_createConnection, _data, _reply, 0);
_reply.readException();
_result = com.unkzdomain.fbeemer.service.aidl.IXmppConnection.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Get the roster of the user
     */
public com.unkzdomain.fbeemer.service.aidl.IRoster getRoster() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.unkzdomain.fbeemer.service.aidl.IRoster _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRoster, _data, _reply, 0);
_reply.readException();
_result = com.unkzdomain.fbeemer.service.aidl.IRoster.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Connect and login synchronously on the server.
     */
public void connectSync() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connectSync, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Connect and login asynchronously on the server.
     */
public void connectAsync() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connectAsync, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Disconnect from the server
     */
public void disconnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Get the chat manager.
     */
public com.unkzdomain.fbeemer.service.aidl.IChatManager getChatManager() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.unkzdomain.fbeemer.service.aidl.IChatManager _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getChatManager, _data, _reply, 0);
_reply.readException();
_result = com.unkzdomain.fbeemer.service.aidl.IChatManager.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Change the status of the user.
     * @param status the status to set
     * @param msg the message state to set
     */
public void changeStatus(int status, java.lang.String msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(status);
_data.writeString(msg);
mRemote.transact(Stub.TRANSACTION_changeStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendPresencePacket(com.unkzdomain.fbeemer.service.PresenceAdapter presence) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((presence!=null)) {
_data.writeInt(1);
presence.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendPresencePacket, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * make a jingle audio call
     * @param jid the receiver id
     */
public void call(java.lang.String jid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jid);
mRemote.transact(Stub.TRANSACTION_call, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
      * get the user vcard avatar
      * @param jid the user jid
      */
public byte[] getVcardAvatar(java.lang.String jid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jid);
mRemote.transact(Stub.TRANSACTION_getVcardAvatar, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_createConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getRoster = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_connectSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_connectAsync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getChatManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_changeStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_sendPresencePacket = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_call = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getVcardAvatar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
/**
     * Get the XmppConnection of the facade.
     */
public com.unkzdomain.fbeemer.service.aidl.IXmppConnection createConnection() throws android.os.RemoteException;
/**
     * Get the roster of the user
     */
public com.unkzdomain.fbeemer.service.aidl.IRoster getRoster() throws android.os.RemoteException;
/**
     * Connect and login synchronously on the server.
     */
public void connectSync() throws android.os.RemoteException;
/**
     * Connect and login asynchronously on the server.
     */
public void connectAsync() throws android.os.RemoteException;
/**
     * Disconnect from the server
     */
public void disconnect() throws android.os.RemoteException;
/**
     * Get the chat manager.
     */
public com.unkzdomain.fbeemer.service.aidl.IChatManager getChatManager() throws android.os.RemoteException;
/**
     * Change the status of the user.
     * @param status the status to set
     * @param msg the message state to set
     */
public void changeStatus(int status, java.lang.String msg) throws android.os.RemoteException;
public void sendPresencePacket(com.unkzdomain.fbeemer.service.PresenceAdapter presence) throws android.os.RemoteException;
/**
     * make a jingle audio call
     * @param jid the receiver id
     */
public void call(java.lang.String jid) throws android.os.RemoteException;
/**
      * get the user vcard avatar
      * @param jid the user jid
      */
public byte[] getVcardAvatar(java.lang.String jid) throws android.os.RemoteException;
}

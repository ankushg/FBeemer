/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/amgupt01/Desktop/beem/src/com/unkzdomain/fbeemer/service/aidl/IMessageListener.aidl
 */
package com.unkzdomain.fbeemer.service.aidl;
public interface IMessageListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.unkzdomain.fbeemer.service.aidl.IMessageListener
{
private static final java.lang.String DESCRIPTOR = "com.unkzdomain.fbeemer.service.aidl.IMessageListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.unkzdomain.fbeemer.service.aidl.IMessageListener interface,
 * generating a proxy if needed.
 */
public static com.unkzdomain.fbeemer.service.aidl.IMessageListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.unkzdomain.fbeemer.service.aidl.IMessageListener))) {
return ((com.unkzdomain.fbeemer.service.aidl.IMessageListener)iin);
}
return new com.unkzdomain.fbeemer.service.aidl.IMessageListener.Stub.Proxy(obj);
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
case TRANSACTION_processMessage:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.aidl.IChat _arg0;
_arg0 = com.unkzdomain.fbeemer.service.aidl.IChat.Stub.asInterface(data.readStrongBinder());
com.unkzdomain.fbeemer.service.Message _arg1;
if ((0!=data.readInt())) {
_arg1 = com.unkzdomain.fbeemer.service.Message.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.processMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_stateChanged:
{
data.enforceInterface(DESCRIPTOR);
com.unkzdomain.fbeemer.service.aidl.IChat _arg0;
_arg0 = com.unkzdomain.fbeemer.service.aidl.IChat.Stub.asInterface(data.readStrongBinder());
this.stateChanged(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.unkzdomain.fbeemer.service.aidl.IMessageListener
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
	 * This method is executed when a chat receive a message.
	 * @param chat the chat receiving the message.
	 * @param msg the message received in the chat.
	 */
public void processMessage(com.unkzdomain.fbeemer.service.aidl.IChat chat, com.unkzdomain.fbeemer.service.Message msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
if ((msg!=null)) {
_data.writeInt(1);
msg.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_processMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * This method is executed when a new ChatState is received by the chat.
	 * You can use IChat.getState() in order to get the new state.
	 * @param chat the chat changed.
	 */
public void stateChanged(com.unkzdomain.fbeemer.service.aidl.IChat chat) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((chat!=null))?(chat.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_stateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_processMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	 * This method is executed when a chat receive a message.
	 * @param chat the chat receiving the message.
	 * @param msg the message received in the chat.
	 */
public void processMessage(com.unkzdomain.fbeemer.service.aidl.IChat chat, com.unkzdomain.fbeemer.service.Message msg) throws android.os.RemoteException;
/**
	 * This method is executed when a new ChatState is received by the chat.
	 * You can use IChat.getState() in order to get the new state.
	 * @param chat the chat changed.
	 */
public void stateChanged(com.unkzdomain.fbeemer.service.aidl.IChat chat) throws android.os.RemoteException;
}

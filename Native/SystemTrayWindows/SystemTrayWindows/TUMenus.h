#include "Stdafx.h"

#include <jni.h>

namespace SystemTrayWindows {

	public ref class TUMenuItem {
	private:
		MenuItem^ item;
		void handler(Object^ sender, EventArgs^ e);

		JavaVM* savedVM;
		JNIEnv * ENV;
	public:
		TUMenuItem(long id);
		long id;
		void setJava(JNIEnv* env, jobject o);
		void setTitle(String^ title);
		MenuItem^ getMenuItem();
		jobject saved_listener_instance;
	};

	public ref class TUSubMenu {
	private:
		MenuItem^ menu;
	public:
		long id;

		TUSubMenu(long id);
		MenuItem^ getMenuItem();

		void addItem(MenuItem^ item);
		void insertItem(MenuItem^ item, int index);
		void removeItem(MenuItem^ item);
		void removeItem(int index);
		void clearMenu();

		void setTitle(String^ title);
	};

	public ref class TUMenu {
	private:
		ContextMenu^ menu;
	public:
		long id;

		TUMenu(long id);
		ContextMenu^ getConextMenu();

		void addItem(MenuItem^ item);
		void insertItem(MenuItem^ item, int index);
		void removeItem(MenuItem^ item);
		void removeItem(int index);
		void clearMenu();
	};

	public ref class TUSystemTrayItem : public Object {
	private:
		NotifyIcon^ item;
	public:
		long id;

		TUSystemTrayItem(long id);

		NotifyIcon^ getNotifyIcon();

		void setIcon(Icon^ icon);
		void setToolTip(String^ text);
		void setMenu(TUMenu^ menu);

		void show();
		void dismiss();
	};

	public ref class TUNotification{
	private:
		void handler(Object^ sender, EventArgs^ e);
		void handlerC(Object^ sender, EventArgs^ e);
		NotifyIcon^ icon;
		EventHandler^ h;
		EventHandler^ hc;
		JavaVM* savedVM;
		JNIEnv * ENV;
	public:
		TUNotification(long id);
		long id;

		void show(String^ title, String^ message, TUSystemTrayItem^ item);

		void setJava(JNIEnv* env, jobject o);
		jobject saved_listener_instance;
	};
}
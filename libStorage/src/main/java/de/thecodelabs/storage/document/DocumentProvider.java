package de.thecodelabs.storage.document;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Verwaltet alle Documenthandler
 *
 * @author tobias
 */
public class DocumentProvider
{

	/**
	 * Model - Handler
	 */
	private static HashMap<Class<?>, HashMap<String, DocumentHandlerHelper>> handlers;

	static
	{
		handlers = new HashMap<>();
	}

	/**
	 * Enthält Informationen über einen Handler / Methode
	 *
	 * @author tobias
	 */
	private static class DocumentHandlerHelper
	{
		private Method worker;
		private Object handler;

		public DocumentHandlerHelper(Method worker, Object handler)
		{
			this.worker = worker;
			this.handler = handler;
		}

		@Override
		public String toString()
		{
			return "[" + "method=" + worker.getName() + ", handler=" + handler.getClass().getCanonicalName() + "]";
		}
	}

	/**
	 * Fügt eine Klasse als handler mit ihren Methoden hinzu
	 *
	 * @param documentHandler Handler Klasse
	 */
	public static void registerHandler(Object documentHandler)
	{
		// Methoden der Klasse
		for(Method method : documentHandler.getClass().getDeclaredMethods())
		{
			DocumentHandler annotation = method.getAnnotation(DocumentHandler.class);
			// Methode ist als Documenthandler bezeichnet
			if(annotation != null)
			{
				// Return Type muss Boolean sein
				if(method.getReturnType() == Boolean.TYPE)
				{
					// Parameter Test (Path, <? implements Model>)
					Class<?>[] args = method.getParameterTypes();
					if(args.length == 2)
					{
						// Parameter 1 ist ein Path
						if(!args[0].equals(Path.class))
						{
							return;
						}
						Class<?> model = args[1];
						// Parameter 2 implements Model
						for(Class<?> clazz : model.getInterfaces())
						{
							if(clazz.equals(Model.class))
							{
								DocumentHandlerHelper documentHandlerHelper = new DocumentHandlerHelper(method, documentHandler);
								if(!handlers.containsKey(model))
									handlers.put(model, new HashMap<>());
								handlers.get(model).put(annotation.type(), documentHandlerHelper);
								break;
							}
						}
					}

				}
			}
		}
	}

	/**
	 * Speicher ein Document im Angegebenen Format. Dafür wird der Richtige
	 * Handler gesucht
	 *
	 * @param path  Speicherort
	 * @param model Dateiinhalt - Datenmodell
	 * @param type  Dateitype (z.B. PDF)
	 * @return <code>true</code> Speichern Erfolgreich
	 * @throws Exception Zugriffsfehler
	 */
	public static boolean save(Path path, Model model, String type) throws Exception
	{
		// Fehlende Dateiendung		
		if(Files.notExists(path))
		{
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}

		if(handlers.containsKey(model.getClass()))
		{
			HashMap<String, DocumentHandlerHelper> handlers = DocumentProvider.handlers.get(model.getClass());

			if(handlers.containsKey(type))
			{
				DocumentHandlerHelper handler = handlers.get(type);
				try
				{
					return (Boolean) handler.worker.invoke(handler.handler, path, model);
				}
				catch(InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}

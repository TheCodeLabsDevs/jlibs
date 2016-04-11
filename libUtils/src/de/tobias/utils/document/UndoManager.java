package de.tobias.utils.document;

import java.util.Stack;

import com.google.common.annotations.Beta;

@Beta
public class UndoManager {

	private static UndoManager defaultUndomanager;

	static {
		defaultUndomanager = new UndoManager();
	}

	private Stack<UndoWorker> undoStack;
	private Stack<UndoWorker> redoStack;

	private boolean isUndoing;
	private boolean isRedoing;

	public interface UndoWorker {
		public void undo();
	}

	public static UndoManager getDefaultUndomanager() {
		return defaultUndomanager;
	}

	public UndoManager() {
		undoStack = new Stack<>();
		redoStack = new Stack<>();
	}

	public void registerUndo(UndoWorker worker) {
		if (isUndoing) {
			redoStack.push(worker);
		} else {
			undoStack.push(worker);
		}

		if (!isUndoing && !isRedoing) {
			redoStack.clear();
		}
	}

	public void undo() {
		if (canUndo()) {
			isUndoing = true;
			undoStack.pop().undo();
			isUndoing = false;
		}
	}

	public void redo() {
		if (canRedo()) {
			isRedoing = true;
			undoStack.pop().undo();
			isRedoing = false;
		}
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public boolean canRedo() {
		return !redoStack.isEmpty();
	}
}

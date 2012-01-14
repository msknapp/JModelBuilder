package com.knapptech.jmodel.model;

public class MBModifiers {
	private boolean _abstract = false;
	private boolean _synchronized = false;
	private boolean _static = false;
	private Visibility visibility = Visibility.PUBLIC;
	private boolean _volatile = false;
	private boolean _final = false;
	private boolean _transient = false;
	
	public MBModifiers() {
		
	}

	public MBModifiers(MBModifiers modifiers) {
		this._abstract = modifiers._abstract;
		this._synchronized = modifiers._synchronized;
		this._static = modifiers._static;
		this.visibility = modifiers.visibility;
		this._volatile = modifiers._volatile;
		this._final = modifiers._final;
		this._transient = modifiers._transient;
	}

	public boolean isAbstract() {
		return _abstract;
	}

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public boolean isSynchronized() {
		return _synchronized;
	}

	public void setSynchronized(boolean _synchronized) {
		this._synchronized = _synchronized;
	}

	public boolean isStatic() {
		return _static;
	}

	public void setStatic(boolean _static) {
		this._static = _static;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public boolean isVolatile() {
		return _volatile;
	}

	public void setVolatile(boolean _volatile) {
		this._volatile = _volatile;
	}

	public boolean isFinal() {
		return _final;
	}

	public void setFinal(boolean _final) {
		this._final = _final;
	}

	public boolean isTransient() {
		return _transient;
	}

	public void setTransient(boolean _transient) {
		this._transient = _transient;
	}
	
	public String toString() {
		return visibility.getText()+" "+
				(_synchronized ? "synchronized" : "")+
				(_static ? "static" : "")+
				(_abstract ? "abstract" : "")+
				(_transient ? "transient" : "")+
				(_volatile ? "volatile" : "");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (_abstract ? 1231 : 1237);
		result = prime * result + (_final ? 1231 : 1237);
		result = prime * result + (_static ? 1231 : 1237);
		result = prime * result + (_synchronized ? 1231 : 1237);
		result = prime * result + (_transient ? 1231 : 1237);
		result = prime * result + (_volatile ? 1231 : 1237);
		result = prime * result
				+ ((visibility == null) ? 0 : visibility.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBModifiers other = (MBModifiers) obj;
		if (_abstract != other._abstract)
			return false;
		if (_final != other._final)
			return false;
		if (_static != other._static)
			return false;
		if (_synchronized != other._synchronized)
			return false;
		if (_transient != other._transient)
			return false;
		if (_volatile != other._volatile)
			return false;
		if (visibility != other.visibility)
			return false;
		return true;
	}

	public void makePublic() {
		this.visibility = Visibility.PUBLIC;
	}

	public void makePrivate() {
		this.visibility = Visibility.PRIVATE;
	}

	public void makePackage() {
		this.visibility = Visibility.PACKAGEPRIVATE;
	}

	public void makePackagePrivate() {
		this.visibility = Visibility.PACKAGEPRIVATE;
	}

	public void makeProtected() {
		this.visibility = Visibility.PROTECTED;
	}

	public boolean isPublic() {
		return this.visibility == Visibility.PUBLIC;
	}

	public boolean isPrivate() {
		return this.visibility == Visibility.PRIVATE;
	}

	public boolean isProtected() {
		return this.visibility == Visibility.PROTECTED;
	}

	public boolean isPackagePrivate() {
		return this.visibility == Visibility.PACKAGEPRIVATE;
	}
}
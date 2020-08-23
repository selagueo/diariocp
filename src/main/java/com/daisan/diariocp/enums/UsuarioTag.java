package com.daisan.diariocp.enums;

public enum UsuarioTag {
	ADMIN("Administrador"),EDITOR("Editor");

	private String usuarioTag;
	

	private UsuarioTag(String usuarioTag) {
		this.usuarioTag=usuarioTag;
	}

	public String getUsuarioTag() {
		return usuarioTag;
	}

	public String toString() {
		return usuarioTag;
	}
}
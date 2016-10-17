package Proyecto.Cluedo.Datos;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Lugar extends Cartas{
	public Lugar(String rutaicono,String nombre){
	super();
	// TODO Auto-generated constructor stub
	this.rutaIcono=rutaicono;
	icono.setSize(95, 152);
	ImageIcon a=new ImageIcon(Lugar.class.getResource(rutaicono ) );
	ImageIcon b=new ImageIcon(a.getImage().getScaledInstance(icono.getWidth(), icono.getHeight(),Image.SCALE_DEFAULT ));
	icono.setIcon( b);
	this.nombre=nombre;
}
}
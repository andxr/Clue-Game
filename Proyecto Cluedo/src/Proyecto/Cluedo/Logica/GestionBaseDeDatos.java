package Proyecto.Cluedo.Logica;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import Proyecto.Cluedo.Datos.*;



public class GestionBaseDeDatos {

	/**
	 * Parametro que contiene el logger para ir viendo lo que pasa
	 */
	
	private static Logger logger = Logger.getLogger(GestionBaseDeDatos.class.getName());
	
	/**
	 * Parametro que contiene el driver para utilizar la base de datos de heroku
	 */
	
	private static final String DRIVER = "org.postgresql.Driver";   
	
	/**
	 * Parametro que contiene el enlace a la base de datos 
	 */
	
	private static final String URL = "jdbc:postgresql://ec2-54-228-214-46.eu-west-1.compute.amazonaws.com:5432/d1uo969itg8nsu?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";   		
	
	/**
	 * Parametro que contiene el nombre de usuario 
	 */
	
	private static final String USERNAME = "ssdiyqrzfdvpfe";   
	
	/**
	 * Parametro que contiene la contrase�a 
	 */
	
	private static final String PASSWORD = "M8Wk6yhGomz7HKFTBopcsYpMfM"; 
	
	/**
	 * Metodo que devuelve un statement para usar la base de datos
	 * @param con Parametro que contiene la conexion con la base 
	 * @return Devuelve un statement para usar con la base 
	 */
	
	public static Statement usarBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			return statement;
		} catch (SQLException e) {
			logger.log( Level.SEVERE, "Error en el establecimiento de la conexion con la base");
			return null;
		}
	}
	
	
	/**
	 * Metodo que sirve para inicializar la base de datos
	 * @return Devuelve la conexion con la base de datos 
	 */
	
	public static Connection inicializarLaBase (){
		try{
			Class.forName(DRIVER);
			
			Connection conexion = DriverManager.getConnection(URL,USERNAME,PASSWORD);
			logger.log(Level.INFO, "Inicializada la base de datos");
			return conexion;
			
		}catch (Exception e){
			logger.log(Level.SEVERE,"Error en la inicializacion de la base de datos");
			return null;
		}
	}
	
	/**
	 * Metodo que sirve para introducir un update
	 * @param creacion Parametro que contiene el comando SQL
	 * @param conexion Parametro que contiene la conexion con la base de datos 
	 * @return Devuelve el statement que genera el update 
	 */
	
	public static Statement crearTabla (String creacion , Connection conexion){
		
		try{
			Statement statement = conexion.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate(creacion);
			
			logger.log(Level.INFO, "Creada la tabla: "+creacion);
			
			return statement;
		}catch (Exception e){
			logger.log(Level.SEVERE, "Error en la creacion de la tabla");
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Metodo que sirve para crear la tabla de usuario 
	 * @param conexion Parametro que contiene la conexion con la base de datos 
	 * @return Devuelve el statament que crea la tabla 
	 */
	
	public static Statement crearTablaUsuario (Connection conexion){

		try{
			
			Statement statement = conexion.createStatement();
			
			try{
			statement.executeUpdate("CREATE TABLE USUARIO (NOMBRE text,APELLIDO text,NOMBREUSUARIO text not null primary key,EMAIL text,CONTRASE�A text,GENERO text,FECHANACIMIENTO bigint,PREGUNTA integer,RESPUESTA text, FECHAULTIMOLOGIN bigint,IMAGENPERFIL bytea,PUNTUACION bigint)");
			}catch (SQLException h){
				logger.log(Level.WARNING, "La tabla ya esta creada");
				return null;
			}
			logger.log(Level.INFO,"Se ha creado una tabla" );
			return statement;
		}catch (Exception e){
			logger.log(Level.SEVERE, "Error en la creacion de la tabla");
			return null;
		}
	}
	
	
	public static boolean insertarUsuario( Connection conexion, Usuario u ) {
		
		
		try {
			
			Image image = u.getImagenPerfil().getImage();
            BufferedImage bImage = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
            Graphics bg = bImage.getGraphics();
            bg.drawImage(image,0,0,null);
            bg.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bImage,"jpeg",out);
            byte[] buf = out.toByteArray();
            ByteArrayInputStream inStream = new ByteArrayInputStream(buf);
           
			
			
			/*File file = new File(u.getImagenPerfil().toString().substring(6));
			FileInputStream fils = new FileInputStream(file);
			*/
			PreparedStatement ps = conexion.prepareStatement("INSERT INTO USUARIO VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			ps.setString(1, u.getNombre());
			ps.setString(2, u.getApellidos());
			ps.setString(3, u.getUsuario());
			ps.setString(4, u.getEmail());
			ps.setString(5, u.getContrase�a());
			ps.setString(6, ""+u.getGenero());
			ps.setLong(7, u.getFechaNacimeinto().getTime());
			ps.setInt(8, u.getPregunta());
			ps.setString(9, u.getRespuesta());
			ps.setLong(10, u.getConexion().getTime());
			
			
			ps.setBinaryStream(11, inStream	,inStream.available());
			ps.setLong(12, u.getPuntuacion());
			ps.executeUpdate();
			ps.close();
			
			logger.log( Level.INFO, "Se ha a�adido la fila");
			return true;
		} catch (Exception e) {
			logger.log( Level.SEVERE, "Error al insertar la fila");
			e.printStackTrace();
			return false;
		}
	}
	
	public static void cerrarBD (Connection con, Statement st){
		try {
			if (st!=null){
				st.close();
				logger.log(Level.INFO, "Se ha cerrado el statement");
			}
			
			if (con!=null){
				con.close();
				logger.log(Level.INFO, "Se ha cerrado la conexion");
			}
		}catch (Exception e){
			logger.log(Level.SEVERE, "No se ha podido realizar la operacion de cierre");
		}
	}
	
	
	 
	public  ArrayList<Usuario> consultaATabla(Connection conexion, String seleccion ) {
		
		ArrayList<Usuario> ret = new ArrayList<>();
		
		try {
			
			Statement statement = conexion.createStatement();
			
			String sentSQL = "SELECT * FROM USUARIO";
			
			if (seleccion!=null && !seleccion.equals(""))
				
				sentSQL = sentSQL + " WHERE " + seleccion;
			
			
			ResultSet rs = statement.executeQuery( sentSQL );
			
			while (rs.next()) {
				Usuario u = new Usuario();
				
				u.setNombre( rs.getString( "NOMBRE" ));
				u.setContrase�a(rs.getString( "CONTRASE�A" ));
				u.setApellidos( rs.getString( "APELLIDO" ));
				u.setUsuario( rs.getString( "NOMBREUSUARIO" ));
				u.setEmail( rs.getString( "EMAIL" ));
				u.setPregunta(rs.getInt("PREGUNTA"));
				u.setRespuesta(rs.getString("RESPUESTA"));
				if (rs.getString("GENERO").equals("HOMBRE")){
					u.setGenero(Genero.HOMBRE);
				}else{
					u.setGenero(Genero.MUJER);
				}
				u.setConexion(new Date(rs.getLong("FECHAULTIMOLOGIN")));
				u.setFechaNacimeinto(new Date (rs.getLong("FECHANACIMIENTO")));
				
				ImageIcon imagen=new ImageIcon();
				/** Blob blob = rs.getBlob("IMAGENPERFIL");
				 byte[] data = blob.getBytes(1, (int)blob.length());
				 img = ImageIO.read(new ByteArrayInputStream(data));
				 try {
				 img = ImageIO.read(new ByteArrayInputStream(data));
				 } catch (IOException ex) {
					 
				 }
				*/
				
				byte[] imgBytes = rs.getBytes(11);
				
				BufferedImage img=null;
				img = ImageIO.read(new ByteArrayInputStream(imgBytes));
				
				 imagen.setImage(img);
				 
				u.setImagenPerfil(imagen);
				
				u.setPuntuacion(rs.getLong("PUNTUACION"));

				ret.add( u );
			}
			rs.close();
			return ret;
		} catch (Exception e) {  
			logger.log(Level.WARNING, "No se entiende la expresion que se introduce");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Metodo que sirve para insertar una partida a la base de datos 
	 * @param conexion Parametro que contiene la conexion con la base de datos 
	 * @param p Parametro que contiene la partida que se desea a�adir 
	 * @return Devuelve true si se a�ade 
	 */
	
	public boolean insertarPartida (Connection conexion,Partida p){
		
		String sql="";
		
		try{
			
			Statement statement = conexion.createStatement();
			
			sql="INSERT INTO PARTIDA VALUES ('"+p.getNombre()+"',"+p.getCodigo()+","+p.getNumeroJugadoresMaximo()+","+p.getNumeroJugadoresActual()+","+p.getPosicionBarco()+",'"+p.getMensajeCartel()+"')";
			
			statement.executeUpdate(sql);
			
			logger.log(Level.INFO, "Se ha a�adido la partida: "+sql);
			
			statement.close();
			
			return true;
			
		}catch (Exception e){
			logger.log(Level.SEVERE, "Erro el insertar la partida : "+sql);
			e.printStackTrace();
			return false;
		}
		
		
		
	}
	
	/**
	 * Metodo que sirve para borrar una lista de la base de datos 
	 * @param conexion Parametro que contiene la conexion con la base
	 * @param tabla Parametro que contiene de la tabla que se desea borrar
	 * @param condicion Parametro que contiene la condicion para borrar 
	 * @return Devuelve true si se ha borrado 
	 */
	
	public boolean borrarPartida (Connection conexion,String tabla,String condicion){
		
		String sql = "";
		
		try {
			
			sql="DELETE FROM "+tabla+" WHERE "+condicion;
			
			Statement statement = conexion.createStatement();
			
			statement.executeUpdate(sql);
			
			logger.log(Level.INFO, "Se ha borrado la partida: "+sql);
			
			statement.close();
			
			return true;
			
		}catch (Exception e){
			logger.log(Level.SEVERE, "No se ha conseguido borrar la partida");
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	public ArrayList <Integer> obtenerCodigoPartidas (Connection conexion){
		
		ArrayList <Integer> listadeCodigos = new ArrayList<Integer>();
		
		String sql="";
		
		try{
			
			Statement statement = conexion.createStatement();
			
			sql ="SELECT CODIGO FROM PARTIDA ORDER BY CODIGO";
			
			ResultSet rs = statement.executeQuery(sql);
			
			while (rs.next()){
				
				int indice=rs.getInt(1);
				System.out.println(indice);
				listadeCodigos.add(indice);
			}
			
			rs.close();
			
			statement.close();
			return listadeCodigos;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void modificar (Connection conexion,String tabla,String actualizacion,String condicion){
		String sql="";
		
		try{
			
			Statement statement = conexion.createStatement();
			
			sql="UPDATE "+tabla+" SET "+actualizacion+" WHERE "+condicion;
			
			statement.executeUpdate(sql);
			
			logger.log(Level.INFO, "Se ha modificado la linea: "+sql);
			
			statement.close();
			
			
			
		}catch (Exception e){
			logger.log(Level.SEVERE,"Error a la hora de modificar la tabla");
			e.printStackTrace();
			
		}
		
		
	}
	
}

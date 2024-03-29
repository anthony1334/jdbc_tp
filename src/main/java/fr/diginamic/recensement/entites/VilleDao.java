package fr.diginamic.recensement.entites;

import org.mariadb.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class VilleDao {
        Connection connection=null;
        Statement stat = null;
        ResultSet curseur= null;
 private void setConnection(){
     try {
         DriverManager.registerDriver(new Driver());
         connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
     } catch (SQLException e) {
         System.out.println(e.getMessage());
     }
 }

    /**
     * Recupere toute les villes
     * @return List<Ville>
     */
    public List<Ville> extraire(){

            try {
                DriverManager.registerDriver(new Driver());
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
                stat = connection.createStatement();
                curseur = stat.executeQuery("SELECT * FROM VILLES ");

                ArrayList<Ville> villes = new ArrayList<>();
                while (curseur.next()) {
                    String id = curseur.getString("ID_VILLES");
                    String nom = curseur.getString("NOM");

                    Ville villeCourante = new Ville(id,nom);
                    villes.add(villeCourante);

                }
                for (Ville ville : villes) {
                    System.out.println(ville);
                }
                System.out.println(connection);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }finally {
                try {
                    if(curseur != null){
                        curseur.close();
                    }
                    stat.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }


            return null;
        }


    /**
     * Méthode qui insert une nouvelle ville
     * @param ville
     */
    public void insert(Ville ville){
            Connection connection=null;
            String nom = ville.getNom();
            if(ville.getNom().contains("'")){
                nom= nom.replaceAll("'","'' ");
            }
            String requete = "INSERT INTO VILLES (code_commune, code_departement, code_region, nom, population) VALUES ('"+ ville.getCodeVille() +"','"+ ville.getCodeDepartement() +"','"+ ville.getCodeRegion() +"','"+ nom +"','"+ville.getPopulation() +"')";
            try {
                DriverManager.registerDriver(new Driver());
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
                System.out.println(connection);
                Statement stat = connection.createStatement();



                stat.executeUpdate(requete);

            } catch (SQLException e) {
                System.out.println(requete);
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    /**
     *  Permet de faire une mise  à jour dans la table ville
     * @param ancienNom
     * @param nouveauNom
     * @return int
     */
        public int update(String ancienNom, String nouveauNom){
            Connection connection=null;
            Statement stat = null;
            int nb= 0;
            try {
                DriverManager.registerDriver(new Driver());
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
                System.out.println(connection);
                stat = connection.createStatement();
                nb= stat.executeUpdate("UPDATE VILLES SET NOM='"+ nouveauNom + "' WHERE NOM ='" + ancienNom+ "'" );
                //UPDATE FOURNISSEUR SET NOM = 'le nouveaunom' WHERE NOM =' ancienNom';

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }finally {

                try {
                    stat.close();
                    connection.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }

            }
            return nb ;
        }

    /**
     *  Permet de supprimer une ville
     * @param ville
     * @return boolean
     */

    public boolean delete(Ville ville){

            Connection connection=null;
            Statement stat = null;

            try {
                DriverManager.registerDriver(new Driver());
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
                System.out.println(connection);
                stat = connection.createStatement();
                stat.executeUpdate("DELETE FROM VILLES WHERE ID_VILLES =" + ville.getCodeVille() );

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }finally {

                try {
                    stat.close();
                    connection.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }

            }
            return true;
        }

    /**
     *
     * @param recensement
     * @return liste ville
     */

    public int insertVille(Recensement recensement){
        List<Ville> villes = recensement.getVilles();
        for (Ville ville : recensement.getVilles()) {

            this.insert(ville);

        }

        System.out.println(villes.size());


        return villes.size();
    }

    private Boolean hasVille(String code, HashSet <Ville> villes){
        for (Ville ville : villes) {
            if(code.equals(ville.getCodeVille()) ){
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne la population d'une ville donnée
     * @param ville
     * @return int
     */

    public int populationVille(String ville){

        int population=0;

     /*   if(ville.contains("'")){
            ville= ville.replaceAll("'","'' ");
        }*/
        String requete = " select population from villes where nom like ?";

        try {
            setConnection();
            PreparedStatement selectVille = this.connection.prepareStatement(requete);
            selectVille.setString(1,ville);
            System.out.println(this.connection);




           ResultSet rs = selectVille.executeQuery();
         rs.next();
             population = rs.getInt("population");





        } catch (SQLException e) {
            System.out.println(requete);
            System.out.println(e.getMessage());
        }
        finally {
            try {
               this.connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return population;
    }

    /**
     * Retourne les N plus grandes villes de france
     * @param nombre
     * @return List<Ville
     */

    public List<Ville>  topNVilleFrance( int nombre){
        Connection connection=null;
        String requete =(" select villes.* from villes order by  population desc limit " + nombre);
        ArrayList<Ville> listeVille = new ArrayList<Ville>();
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/recensement", "root", "root");
            System.out.println(connection);
            Statement stat = connection.createStatement();


            ResultSet rs = stat.executeQuery(requete);

            while(rs.next()){
                listeVille.add(new Ville(
                                rs.getString("code_region"),
                                rs.getString("code_departement"),
                                rs.getString("code_commune"),
                                rs.getString("nom"),
                                rs.getInt("population")

                        )
                );

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(requete);
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return listeVille;
    }


}



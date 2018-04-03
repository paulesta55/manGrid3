package epp;

import org.seamcat.model.plugin.Config;

/**
 * Cette interface correspond à une IHM dans laquelle l'utilisateur peut choisir les différents paramètres du
 * post processing.
 * Ici :
 * <ul>
 *     <li>la position de la victime</li>
 *     <li>la largeur des rues</li>
 *     <li>le nombre de blocks</li>
 * </ul>
 * @author Paul Estano
 */
public interface EppUIInput{
    enum VLT_position {Outdoor,Indoor}

    @Config(order = 1, name = "Select VLT_Position")//cette annotation permet de créer un bouton pour l'IHM
    VLT_position vLT_position();
    VLT_position vLT_position = VLT_position.Indoor;

    @Config(order = 3, name = "Width of the street in meters") int road_width();
    int road_width = 30;

    @Config(order = 5, name = "Numbers of blocks ( for x and y) ") int nb_blocks();
    int nb_blocks = 5;
}

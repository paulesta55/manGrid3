package epp;

import org.seamcat.model.plugin.Config;

public interface EppUIInput{
    /*Cette interface correspond à une IHM dans laquelle l'utilisateur peut choisir les différents paramètres du
    post processing.
    Ici :
    _la position de la victime
    _la largeur des rues
    _le nombre de blocks
     */
    enum VLT_position {Outdoor,Indoor}

    @Config(order = 1, name = "Select VLT_Position")//cette annotation permet de créer un bouton pour l'IHM
    VLT_position vLT_position();
    VLT_position vLT_position = VLT_position.Indoor;

    @Config(order = 3, name = "Width of the street in meters") int road_width();
    int road_width = 30;

    @Config(order = 5, name = "Numbers of blocks ( for x and y) ") int nb_blocks();
    int nb_blocks = 5;
}

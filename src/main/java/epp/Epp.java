package epp;

import org.seamcat.model.Scenario;
import org.seamcat.model.factory.Factory;
import org.seamcat.model.plugin.eventprocessing.EventProcessingPlugin;
import org.seamcat.model.plugin.eventprocessing.PostProcessingTab;
import org.seamcat.model.plugin.eventprocessing.PostProcessingUI;
import org.seamcat.model.plugin.system.ConsistencyCheckContext;
import org.seamcat.model.simulation.consistency.Validator;
import org.seamcat.model.simulation.result.Collector;
import org.seamcat.model.simulation.result.EventResult;
import org.seamcat.model.simulation.result.LinkResult;
import org.seamcat.model.simulation.result.VectorDef;
import org.seamcat.model.types.Description;
import org.seamcat.model.types.InterferenceLink;
import org.seamcat.model.types.result.DescriptionImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Ici on récupère les données de la simulation en implémentant EventProcessingPlugin et on indique les interfaces
 * graphiques utilisées (ici EppUI) en implémentant PostProcessingTab
 * @see org.seamcat.model.plugin.eventprocessing.PostProcessingTab
 * @see org.seamcat.model.plugin.eventprocessing.EventProcessingPlugin
 * @author Paul Estano
 */


public class Epp implements EventProcessingPlugin<Epp.Input>,PostProcessingTab {

    /**
     * à implémenter pour récupérer les positions des différents points (VLR,VLT, ILR, ILT en X et en Y)
     * @param scenario
     * @param eventResult
     * @param input
     * @param resultCollector
     */
    public void evaluate(Scenario scenario, EventResult eventResult, Input input, Collector resultCollector )
    {//on récupère les résultats
        for (InterferenceLink link : scenario.getInterferenceLinks()) {


            LinkResult victimResult = eventResult.getInterferenceLinkResult(link).get(0).getVictimSystemLink();

            resultCollector.add(Input.VLRX,victimResult.rxAntenna().getPosition().getX());
            resultCollector.add(Input.VLRY,victimResult.rxAntenna().getPosition().getY());
            resultCollector.add(Input.VLTX,victimResult.txAntenna().getPosition().getX());
            resultCollector.add(Input.VLTY,victimResult.txAntenna().getPosition().getY());


            for (int i = 0; i < eventResult.getInterferenceLinkResult(link).size(); i++) {
                LinkResult lr= eventResult.getInterferenceLinkResult(link).get(i).getInterferingSystemLink();

                //String iName = link.getInterferer().toString();
                resultCollector.add(Input.ILRX,lr.rxAntenna().getPosition().getX());
                resultCollector.add(Input.ILRY,lr.rxAntenna().getPosition().getY());
                resultCollector.add(Input.ILTX,lr.txAntenna().getPosition().getX());
                resultCollector.add(Input.ILTY,lr.txAntenna().getPosition().getY());

            }
        }

    }

    /**
     * Inutile (pour l'instant...)
     * @param context
     * @param input
     * @param validator
     */
    @Override
    public void consistencyCheck(ConsistencyCheckContext context, Input input, Validator validator){}

    /**
     *
     * @return le nom et la description du plugin
     */
    public Description description(){
        return new DescriptionImpl("Manhattan Grid","grille de manhattan");
    }//descripton du plugin (observable quand on sélectionne le plugin comme "Event Processing" pour la simulation
    public interface Input{//on définit les variables où seront stockés les résultats
        VectorDef VLRX = Factory.results().value("VLR position X","km");
        VectorDef VLRY = Factory.results().value("VLR position Y","km");
        VectorDef VLTX = Factory.results().value("VLT position X","km");
        VectorDef VLTY = Factory.results().value("VLT position Y","km");
        VectorDef ILRX = Factory.results().value("ILR position X","km");
        VectorDef ILRY = Factory.results().value("ILR position Y","km");
        VectorDef ILTX = Factory.results().value("ILT position X","km");
        VectorDef ILTY = Factory.results().value("ILT position Y","km");
    }

    /**
     *
     * @return les interfaces utilisées dans le plugin
     */
    @Override
    public List<Class<? extends PostProcessingUI>> tabs() {
        List<Class<? extends PostProcessingUI>> tabs = new ArrayList<>();
        tabs.add(EppUI.class);//Si l'on veut utiliser une IHM il est nécessaire d'ajouter cette IHM à l'objet de classe List<Class<? extends PostProcessingUI>>
        return tabs;
    }


}
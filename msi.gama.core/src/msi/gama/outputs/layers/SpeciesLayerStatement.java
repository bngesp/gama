/*********************************************************************************************
 * 
 * 
 * 'SpeciesLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.POPULATION, kind = ISymbolKind.LAYER, with_sequence = true, remote_context = true)
@inside(symbols = { IKeyword.DISPLAY, IKeyword.POPULATION })
@facets(value = {
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.TRACE, type = { IType.BOOL, IType.INT }, optional = true, doc = @doc("Allows to aggregate the visualization of agents at each timestep on the display. Default is false. If set to an int value, only the last n-th steps will be visualized. If set to true, no limit of timesteps is applied. ")),
	@facet(name = IKeyword.FADING, type = { IType.BOOL }, optional = true, doc = @doc("Used in conjunction with 'trace:', allows to apply a fading effect to the previous traces. Default is false")),
	@facet(name = IKeyword.SPECIES, type = IType.SPECIES, optional = false),
	@facet(name = IKeyword.ASPECT, type = IType.ID, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.SPECIES)
@doc(value="`agents` allows the modeler to display only the agents that fulfill a given condition.", 
see={IKeyword.DISPLAY,IKeyword.AGENTS,IKeyword.CHART,IKeyword.EVENT,"graphics",IKeyword.GRID_POPULATION,IKeyword.IMAGE,IKeyword.OVERLAY,IKeyword.QUADTREE,IKeyword.POPULATION,IKeyword.TEXT})
public class SpeciesLayerStatement extends AgentLayerStatement {

	private IExecutable aspect;

	protected ISpecies hostSpecies;
	protected ISpecies species;
	protected List<SpeciesLayerStatement> microSpeciesLayers;
	protected List<GridLayerStatement> gridLayers;
	protected List<AbstractLayerStatement> subLayers;

	public SpeciesLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SPECIES).literalValue());
		microSpeciesLayers = new GamaList<SpeciesLayerStatement>();
		gridLayers = new GamaList<GridLayerStatement>();
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		// top level species layer is a direct micro-species of "world_species" for sure
		if ( hostSpecies == null ) {
			hostSpecies = scope.getSimulationScope().getSpecies();
		}

		species = hostSpecies.getMicroSpecies(getName());
		if ( species == null ) { throw GamaRuntimeException.error("not a suitable species to display: " + getName(),
			scope); }
		if ( super._init(scope) ) {
			for ( final SpeciesLayerStatement microLayer : microSpeciesLayers ) {
				microLayer.setHostSpecies(species);
				if ( !scope.init(microLayer) ) { return false; }
			}
		}
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		if ( super._step(scope) ) {
			for ( final SpeciesLayerStatement microLayer : microSpeciesLayers ) {
				scope.step(microLayer);
				// if ( !scope.step(microLayer) ) { return false; }
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean agentsHaveChanged() {
		return true;
		// return population.populationHasChanged();
	}

	@Override
	public List<? extends IAgent> computeAgents(final IScope sim) {
		return GamaList.EMPTY_LIST;
	}

	@Override
	public short getType() {
		return ILayerStatement.SPECIES;
	}

	public List<String> getAspects() {
		return species.getAspectNames();
	}

	@Override
	public void setAspect(final String currentAspect) {
		super.setAspect(currentAspect);
		aspect = species.getAspect(constantAspectName);
	}

	@Override
	public void computeAspectName(final IScope sim) throws GamaRuntimeException {
		if ( aspect != null ) { return; }
		super.computeAspectName(sim);
		aspect = species.getAspect(constantAspectName);
	}

	public IExecutable getAspect() {
		return aspect;
	}

	public void setHostSpecies(final ISpecies hostSpecies) {
		this.hostSpecies = hostSpecies;
	}

	public ISpecies getSpecies() {
		return species;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		final List<SpeciesLayerStatement> microL = new GamaList<SpeciesLayerStatement>();
		final List<GridLayerStatement> gridL = new GamaList<GridLayerStatement>();
		final List<IStatement> aspectStatements = new GamaList();

		for ( final ISymbol c : commands ) {
			if ( c instanceof SpeciesLayerStatement ) {
				microL.add((SpeciesLayerStatement) c);
			} else if ( c instanceof GridLayerStatement ) {
				gridL.add((GridLayerStatement) c);
			} else if ( c instanceof IStatement ) {
				aspectStatements.add((IStatement) c);
			}
		}
		if ( !aspectStatements.isEmpty() ) {
			constantAspectName = "inline";
			IDescription d = DescriptionFactory.create(IKeyword.ASPECT, getDescription(), IKeyword.NAME, "inline");
			aspect = new AspectStatement(d);
			((AspectStatement) aspect).setChildren(aspectStatements);
		}
		setMicroSpeciesLayers(microL);
		setGridLayers(gridL);
	}

	private void setMicroSpeciesLayers(final List<SpeciesLayerStatement> layers) {
		if ( layers == null ) { return; }

		microSpeciesLayers.clear();
		microSpeciesLayers.addAll(layers);
	}

	private void setGridLayers(final List<GridLayerStatement> layers) {
		if ( layers == null ) { return; }

		gridLayers.clear();
		gridLayers.addAll(layers);
	}

	/**
	 * Returns a list of micro-species layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<SpeciesLayerStatement> getMicroSpeciesLayers() {
		return microSpeciesLayers;
	}

	/**
	 * Returns a list of grid layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<GridLayerStatement> getGridLayers() {
		return gridLayers;
	}

	/**
	 * Returns a list of grid and micro-species layers declared as sub-layers.
	 * The grid layers are put ahead in the returned list.
	 * 
	 * @return
	 */
	public List<AbstractLayerStatement> getSubLayers() {
		if ( subLayers == null ) {
			subLayers = new GamaList<AbstractLayerStatement>();
			subLayers.addAll(gridLayers);
			subLayers.addAll(microSpeciesLayers);
		}

		return subLayers;
	}

	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
		return "SpeciesDisplayLayer species: " + getName();
	}
}

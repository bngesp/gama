/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.ISpatialIndex.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.Collection;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 23 f�vr. 2011
 *
 * @todo Description
 *
 */
public interface ISpatialIndex {

	public abstract void insert(IAgent agent);

	public abstract void remove(final Envelope previous, final IAgent agent);

	public abstract IAgent firstAtDistance(IScope scope, final IShape source, final double dist, final IAgentFilter f);

	public abstract Collection<IAgent> firstAtDistance(IScope scope, final IShape source, final double dist, final IAgentFilter f, int number, Collection<IAgent> alreadyChosen );

	
	public abstract Collection<IAgent> allInEnvelope(IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, boolean contained);

	Collection<IAgent> allAtDistance(IScope scope, IShape source, double dist, IAgentFilter f);

	public abstract void dispose();

	public interface Compound extends ISpatialIndex {

		public abstract void add(ISpatialIndex index, IPopulation<? extends IAgent> pop);

		public void remove(final IPopulation<? extends IAgent> species);

		public abstract void updateQuadtree(Envelope envelope);

		public abstract void mergeWith(Compound spatialIndex);

	}

	public abstract Collection<IAgent> allAgents();

}
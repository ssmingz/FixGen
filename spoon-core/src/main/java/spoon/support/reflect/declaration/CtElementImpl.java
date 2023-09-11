/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import codegraph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.ModelElementContainerDefaultCapacities;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtIterator;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.DerivedProperty;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.code.*;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.util.EmptyClearableList;
import spoon.support.util.EmptyClearableSet;
import spoon.support.visitor.HashcodeVisitor;
import spoon.support.visitor.TypeReferenceScanner;
import spoon.support.visitor.equals.CloneHelper;
import spoon.support.visitor.equals.EqualsVisitor;
import spoon.support.visitor.replace.ReplacementVisitor;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.*;

import static spoon.reflect.visitor.CommentHelper.printComment;

/**
 * Contains the default implementation of most CtElement methods.
 *
 */
public abstract class CtElementImpl implements CtElement {
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String ERROR_MESSAGE_TO_STRING = "Error in printing the node. One parent isn't initialized!";
	private static final Factory DEFAULT_FACTORY = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());

	/*********************************************************/
	/******* added by FixGen *********************************/
	/*********************************************************/
	/**
	 * parent node in the abstract syntax tree in codegraph
	 */
	protected CtElementImpl _parent = null;
	/**
	 * control dependency
	 */
	protected CtElementImpl _controlDependency = null;
	/**
	 * data dependency
	 */
	protected Set<CtElementImpl> _dataDependency = new HashSet<>();
	/**
	 * edges
	 */
	public ArrayList<Edge> _inEdges = new ArrayList<>();
	public ArrayList<Edge> _outEdges = new ArrayList<>();
	/**
	 * variable usage scope
	 */
	private Scope _scope = null;
	protected boolean _actionRelated = false;
	public String _graphName = "";
	public int _graphId = -1;

	public static <T> List<T> emptyList() {
		return EmptyClearableList.instance();
	}

	public static <T> Set<T> emptySet() {
		return EmptyClearableSet.instance();
	}

	public static <T> List<T> unmodifiableList(List<T> list) {
		return list==null || list.isEmpty() ? Collections.<T>emptyList() : Collections.unmodifiableList(list);
	}

	/** this field `factory` must be transient in order to allow proper serialization
	 * the factory is restored in all AST nodes in {@link spoon.support.SerializationModelStreamer}
	 */
	protected Factory factory;

	protected CtElement parent;

	@MetamodelPropertyField(role = CtRole.ANNOTATION)
	List<CtAnnotation<? extends Annotation>> annotations = emptyList();

	@MetamodelPropertyField(role = CtRole.COMMENT)
	private List<CtComment> comments = emptyList();

	@MetamodelPropertyField(role = CtRole.POSITION)
	SourcePosition position = SourcePosition.NOPOSITION;

	transient Map<String, Object> metadata;

	public CtElementImpl() {
	}

	@Override
	public String getShortRepresentation() {
		return super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CtElementImpl)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		boolean ret = EqualsVisitor.equals(this, (CtElement) o);
		// neat online testing of core Java contract
		if (ret && (factory!=null && !factory.getEnvironment().checksAreSkipped()) && this.hashCode() != o.hashCode()) {
			throw new IllegalStateException("violation of equal/hashcode contract between \n" + this + "\nand\n" + o + "\n");
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		CtType annot = getFactory().Annotation().get(annotationType);
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().getQualifiedName().equals(annot.getQualifiedName())) {
				return ((CtAnnotation<A>) a).getActualAnnotation(); // warning, here we do heavy and costly work with proxy
			}
		}
		return null;
	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
		CtType annot = getFactory().Annotation().get(annotationType);
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().getQualifiedName().equals(annot.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> getAnnotation(CtTypeReference<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().equals(annotationType)) {
				return (CtAnnotation<A>) a;
			}
		}
		return null;
	}

	@Override
	public List<CtAnnotation<? extends Annotation>> getAnnotations() {
		return unmodifiableList(annotations);
	}

	@Override
	public String getDocComment() {
		for (CtComment ctComment : comments) {
			if (ctComment.getCommentType() == CtComment.CommentType.JAVADOC) {
				return printComment(ctComment);
			}
		}
		return "";
	}

	@Override
	public SourcePosition getPosition() {
		if (position != null) {
			return position;
		}
		return SourcePosition.NOPOSITION;
	}

	@Override
	public int hashCode() {
		HashcodeVisitor pr = new HashcodeVisitor();
		pr.scan(this);
		return pr.getHasCode();
	}

	@Override
	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		if (annotations == null || annotations.isEmpty()) {
			getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.ANNOTATION, this.annotations, new ArrayList<>(this.annotations));
			this.annotations = emptyList();
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.ANNOTATION, this.annotations, new ArrayList<>(this.annotations));
		this.annotations.clear();
		for (CtAnnotation<? extends Annotation> annot : annotations) {
			addAnnotation(annot);
		}
		return (E) this;
	}

	@Override
	public void delete() {
		if (!isParentInitialized()) {
			// already not in a tree, nothing to be deleted
			return;
		}
		//delete is implemented as replace by no element (empty list of elements)
		replace(Collections.<CtElement>emptyList());
	}

	@Override
	public <E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (annotation == null) {
			return (E) this;
		}
		if (this.annotations == CtElementImpl.<CtAnnotation<? extends Annotation>>emptyList()) {
			this.annotations = new ArrayList<>(ModelElementContainerDefaultCapacities.ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		annotation.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.ANNOTATION, this.annotations, annotation);
		this.annotations.add(annotation);
		return (E) this;
	}

	@Override
	public boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (this.annotations == CtElementImpl.<CtAnnotation<? extends Annotation>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.ANNOTATION, annotations, annotations.indexOf(annotation), annotation);
		return this.annotations.remove(annotation);
	}

	@Override
	public <E extends CtElement> E setDocComment(String docComment) {
		for (CtComment ctComment : comments) {
			if (ctComment.getCommentType() == CtComment.CommentType.JAVADOC) {
				ctComment.setContent(docComment);
				return (E) this;
			}
		}
		this.addComment(factory.Code().createComment(docComment, CtComment.CommentType.JAVADOC));
		return (E) this;
	}

	@Override
	public <E extends CtElement> E setPosition(SourcePosition position) {
		if (position == null) {
			position = SourcePosition.NOPOSITION;
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.POSITION, position, this.position);
		this.position = position;
		return (E) this;
	}

	@Override
	public <E extends CtElement> E setPositions(final SourcePosition position) {
		accept(new CtScanner() {
			@Override
			public void enter(CtElement e) {
				e.setPosition(position);
			}
		});
		return (E) this;
	}

	@Override
	public String prettyprint() {
		PrettyPrinter printer = getFactory().getEnvironment().createPrettyPrinterAutoImport();
		return printer.prettyprint(this);
	}

	@Override
	public String toStringDebug() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(getFactory().getEnvironment());
		return printer.scan(this).toString();
	}

	@Override
	public String toString() {
		DefaultJavaPrettyPrinter printer = (DefaultJavaPrettyPrinter) getFactory().getEnvironment().createPrettyPrinter();

		return printer.printElement(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> annotationType) {
		return (List<E>) Query.getElements(this, new AnnotationFilter<>(CtElement.class, annotationType));
	}

	@MetamodelPropertyField(role = CtRole.IS_IMPLICIT)
	boolean implicit = false;

	@Override
	public boolean isImplicit() {
		return implicit;
	}

	@Override
	public <E extends CtElement> E setImplicit(boolean implicit) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_IMPLICIT, implicit, this.implicit);
		this.implicit = implicit;
		return (E) this;
	}

	@Override
	@DerivedProperty
	public Set<CtTypeReference<?>> getReferencedTypes() {
		TypeReferenceScanner s = new TypeReferenceScanner();
		s.scan(this);
		return s.getReferences();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return filterChildren(filter).list();
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return factory.Query().createQuery(this).map(queryStep);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return factory.Query().createQuery(this).map(function);
	}

	@Override
	public <P extends CtElement> CtQuery filterChildren(Filter<P> predicate) {
		return factory.Query().createQuery(this).filterChildren(predicate);
	}

	@Override
	public CtElement getParent() throws ParentNotInitializedException {
		if (parent == null) {
			String exceptionMsg;
			if (this instanceof CtReference) {
				exceptionMsg = "parent not initialized for " + ((CtReference) this).getSimpleName() + "(" + this.getClass() + ")";
			} else {
				SourcePosition pos = getPosition();
				if (this instanceof CtNamedElement) {
					exceptionMsg = ("parent not initialized for " + ((CtNamedElement) this).getSimpleName() + "(" + this.getClass() + ")" + (pos != null ? " " + pos : " (?)"));
				} else {
					exceptionMsg = ("parent not initialized for " + this.getClass() + (pos != null ? " " + pos : " (?)"));
				}
			}
			throw new ParentNotInitializedException(exceptionMsg);
		}
		return parent;
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		this.parent = parent;
		return (E) this;
	}

	@Override
	public boolean isParentInitialized() {
		return parent != null;
	}

	@Override
	public <P extends CtElement> P getParent(Class<P> parentType) {
		CtElement current = this;
		while (current.isParentInitialized()) {
			current = current.getParent();
			if (parentType.isAssignableFrom(current.getClass())) {
				return parentType.cast(current);
			}
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E getParent(Filter<E> filter) {
		CtElement current = this;
		while (current.isParentInitialized()) {
			current = current.getParent();
			try {
				if (filter.matches((E) current)) {
					return (E) current;
				}
			} catch (ClassCastException ignored) {
				// expected, some elements are not of type
			}
		}

		return null;
	}

	@Override
	public boolean hasParent(CtElement candidate) {
		try {
			return this != getFactory().getModel().getUnnamedModule() && (getParent() == candidate || getParent().hasParent(candidate));
		} catch (ParentNotInitializedException e) {
			return false;
		}
	}

	@Override
	public CtRole getRoleInParent() {
		if (isParentInitialized()) {
			EarlyTerminatingScanner<CtRole> ets = new EarlyTerminatingScanner<CtRole>() {
				@Override
				public void scan(CtRole role, CtElement element) {
					if (element == CtElementImpl.this) {
						setResult(role);
						terminate();
					}
					//do not call super.scan, because we do not want scan children
				}
			};
			getParent().accept(ets);
			return ets.getResult();
		}
		return null;
	}

	@Override
	public void updateAllParentsBelow() {
		new ModelConsistencyChecker(getFactory().getEnvironment(), true, true).scan(this);
	}

	@Override
	public Factory getFactory() {
		if (this.factory == null) {
			return DEFAULT_FACTORY;
		}
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public void replace(CtElement element) {
		ReplacementVisitor.replace(this, element);
	}

	@Override
	public <E extends CtElement> void replace(Collection<E> elements) {
		ReplacementVisitor.replace(this, elements);
	}

	@Override
	public <E extends CtElement> E setAllMetadata(Map<String, Object> metadata) {
		if (metadata == null || metadata.isEmpty()) {
			this.metadata = null;
			return (E) this;
		}
		if (this.metadata == null) {
			this.metadata = new HashMap<>();
		} else {
			this.metadata.clear();
		}
		this.metadata.putAll(metadata);
		return (E) this;
	}

	@Override
	public <E extends CtElement> E putMetadata(String key, Object val) {
		if (metadata == null) {
			metadata = new HashMap<>();
		}
		metadata.put(key, val);
		return (E) this;
	}

	@Override
	public Object getMetadata(String key) {
		if (metadata == null) {
			return null;
		}
		return metadata.get(key);
	}

	@Override
	public Map<String, Object> getAllMetadata() {
		if (this.metadata == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(this.metadata);
	}

	@Override
	public Set<String> getMetadataKeys() {
		if (metadata == null) {
			return Collections.emptySet();
		}
		return metadata.keySet();
	}

	@Override
	public List<CtComment> getComments() {
		return unmodifiableList(comments);
	}

	@Override
	public <E extends CtElement> E addComment(CtComment comment) {
		if (comment == null) {
			return (E) this;
		}
		if (this.comments == CtElementImpl.<CtComment>emptyList()) {
			comments = new ArrayList<>(ModelElementContainerDefaultCapacities.COMMENT_CONTAINER_DEFAULT_CAPACITY);
		}
		comment.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.COMMENT, this.comments, comment);
		comments.add(comment);
		return (E) this;
	}

	@Override
	public <E extends CtElement> E removeComment(CtComment comment) {
		if (this.comments == CtElementImpl.<CtComment>emptyList()) {
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.COMMENT, comments, comments.indexOf(comment), comment);
		this.comments.remove(comment);
		return (E) this;
	}

	@Override
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		if (comments == null || comments.isEmpty()) {
			this.comments = emptyList();
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.COMMENT, this.comments, new ArrayList<>(this.comments));
		this.comments.clear();
		for (CtComment comment : comments) {
			addComment(comment);
		}
		return (E) this;
	}

	@Override
	public CtElement clone() {
		return CloneHelper.INSTANCE.clone(this);
	}

	@Override
	public <T> T getValueByRole(CtRole role) {
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(this.getClass(), role);
		return rh.getValue(this);
	}

	@Override
	public <E extends CtElement, T> E setValueByRole(CtRole role, T value) {
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(this.getClass(), role);
		rh.setValue(this, value);
		return (E) this;
	}

	@Override
	public CtPath getPath() {
		return new CtElementPathBuilder().fromElement(this);
	}

	@Override
	public Iterator<CtElement> descendantIterator() {
		return new CtIterator(this);
	}

	@Override
	public Iterable<CtElement> asIterable() {
		return this::descendantIterator;
	}

	@Override
	public ElementSourceFragment getOriginalSourceFragment() {
		SourcePosition sp = this.getPosition();
		CtCompilationUnit compilationUnit = sp.getCompilationUnit();
		if (compilationUnit != null) {
			ElementSourceFragment rootFragment = compilationUnit.getOriginalSourceFragment();
			return rootFragment.getSourceFragmentOf(this, sp.getSourceStart(), sp.getSourceEnd() + 1);
		} else {
			return ElementSourceFragment.NO_SOURCE_FRAGMENT;
		}
	}

	/**
	 * Replace the statement with a CtComment having the statement as text
	 */
	public void comment() {
		if (this instanceof CtStatement && getParent() instanceof CtBlock) {
			if (this instanceof CtComment) {
				return;
			}
			final String stmt = toString();
			if (stmt.contains(CtComment.LINE_SEPARATOR)) {
				this.replace(getFactory().Code().createComment(stmt, CtComment.CommentType.BLOCK)); // Multi line comment
			} else {
				this.replace(getFactory().Code().createInlineComment(stmt + ';')); // Single line comment
			}
		} else {
			throw new UnsupportedOperationException("Only CtStatement within CtBlock or CtBlock as a method body can be commented out");
		}
	}

	@Override
	public List<CtElement> getDirectChildren() {
		List<CtElement> directChildren = new ArrayList<>();
		CtScanner scanner = new CtScanner() {
			@Override
			public void scan(CtElement element) {
				// since we don't call super.scan, this does not further descend in the tree
				if (element != null) {
					directChildren.add(element);
				}
			}
		};

		this.accept(scanner);
		return directChildren;
	}

	/*********************************************************/
	/******* added by FixGen *********************************/
	/*********************************************************/
	public void addOutEdge(Edge edge) {
		_outEdges.add(edge);
	}

	public void addInEdge(Edge edge) {
		_inEdges.add(edge);
	}

	public void setParent(CtElementImpl node) {
		_parent = node;
		this.parent = node;
	}

	public void setControlDependency(CtElementImpl controller) {
		_controlDependency = controller;
		if (controller != null) {
			new ControlEdge(controller, this);
		}
	}

	public void setDataDependency(CtElementImpl controller) {
		if (controller != null) {
			if (controller instanceof CtArrayReadImpl || controller instanceof CtVariableReadImpl
					|| controller instanceof CtFieldReadImpl || controller instanceof CtThisAccessImpl) {
				new DataEdge(controller, this);
			}
			for (Object ch : controller.getDirectChildren()) {  // only for direct children
				if (ch instanceof CtArrayReadImpl || ch instanceof CtVariableReadImpl || ch instanceof CtFieldReadImpl || ch instanceof CtThisAccessImpl) {
					new DataEdge((CtElementImpl) ch, this);
				} else {
					setDataDependency((CtElementImpl) ch);
				}
			}
		}
	}

	public void addDataDepNode(CtElementImpl controller) {
		if (controller != null) {
			if (controller instanceof CtArrayReadImpl || controller instanceof CtVariableReadImpl
					|| controller instanceof CtFieldReadImpl || controller instanceof CtThisAccessImpl) {
				_dataDependency.add(controller);
			}
			for (Object ch : controller.getDirectChildren()) {  // only for direct children
				if (ch instanceof CtArrayReadImpl || ch instanceof CtVariableReadImpl || ch instanceof CtFieldReadImpl || ch instanceof CtThisAccessImpl) {
					_dataDependency.add((CtElementImpl) ch);
				} else {
					_dataDependency.add((CtElementImpl) ch);
				}
			}
		}
	}

	public void setScope(Scope scope) { _scope = scope; }

	public boolean hasASTChildren() {
		for (Edge out : _outEdges) {
			if (out instanceof ASTEdge)
				return true;
		}
		return false;
	}

	public boolean isDependOn(CtElementImpl node) {
		for (CtElementImpl e : this.getDependNodes()) {
			if (e == node)
				return true;
		}
		return false;
	}

	public boolean hasDependencyRelation(CtElementImpl src) {
		// this depends on src
		for (CtElementImpl e : this.getDependNodes()) {
			if (e == src)
				return true;
			Iterator<CtElement> itr = src.descendantIterator();
			while (itr.hasNext()) {
				CtElementImpl c = (CtElementImpl) itr.next();
				if (e == c)
					return true;
			}
		}
		return false;
	}

	public Set<CtElementImpl> getDependNodes() {
		Set<CtElementImpl> all = new LinkedHashSet<>();
		for (Edge e : this._inEdges) {
			if (e.type == Edge.EdgeType.CONTROL_DEP
					|| e.type == Edge.EdgeType.DATA_DEP || e.type == Edge.EdgeType.DEF_USE)
				all.add(e.getSource());
		}
		return all;
	}

	public boolean hasInEdge(CtElementImpl src, Edge.EdgeType type) {
		for (Edge ie : _inEdges) {
			if (ie.getSource()==src && ie.type==type)
				return true;
		}
		return false;
	}

	public boolean hasOutEdge(CtElementImpl tar, Edge.EdgeType type) {
		for (Edge oe : _outEdges) {
			if (oe.getTarget()==tar && oe.type==type)
				return true;
		}
		return false;
	}

	/**
	 * whether is action node or in "after" graph (compared to "before")
	 */
	public boolean isActionRelated() {
		return _actionRelated;
	}

	public void setActionRelated(boolean isActionRel) {
		_actionRelated = isActionRel;
	}

}

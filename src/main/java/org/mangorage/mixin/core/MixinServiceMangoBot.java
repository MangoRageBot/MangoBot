package org.mangorage.mixin.core;

import org.jetbrains.annotations.NotNull;
import org.mangorage.bootstrap.api.loader.MangoLoader;
import org.mangorage.mixin.MixinContainer;
import org.mangorage.mixin.MixinLogger;
import org.mangorage.mixin.transformer.MangoBotTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.transformers.MixinClassReader;
import org.spongepowered.asm.util.ReEntranceLock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public final class MixinServiceMangoBot implements IMixinService, IClassProvider, IClassBytecodeProvider, ITransformerProvider, IClassTracker {
    private final ReEntranceLock lock = new ReEntranceLock(1);
    private final MixinContainer container = new MixinContainer("mangobot");

    @Override
    public String getName() {
        return "MangoBotMixinService";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void prepare() {

    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public void offer(IMixinInternal iMixinInternal) {
        if (iMixinInternal instanceof IMixinTransformerFactory factory) {
            MangoBotTransformer.getInstance().set(factory);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void checkEnv(Object o) {

    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return this.lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return List.of();
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return this.container;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return List.of();
    }

    @Override
    public InputStream getResourceAsStream(String s) {
        final var loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(s);
    }

    @Override
    public String getSideName() {
        return MixinEnvironment.Side.SERVER.name();
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_8;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_22;
    }

    @Override
    public ILogger getLogger(String s) {
        return MixinLogger.get(s);
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public @NotNull Class<?> findClass(final @NotNull String name) throws ClassNotFoundException {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public @NotNull Class<?> findClass(final @NotNull String name, final boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public @NotNull Class<?> findAgentClass(final @NotNull String name, final boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Thread.currentThread().getContextClassLoader());
    }
    //</editor-fold>

    //<editor-fold desc="IClassBytecodeProvider">
    @Override
    public @NotNull ClassNode getClassNode(final @NotNull String name) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, true);
    }

    @Override
    public @NotNull ClassNode getClassNode(final @NotNull String name, final boolean runTransformers) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, runTransformers, 0);
    }

    public @NotNull ClassNode getClassNode(final @NotNull String name, final boolean runTransformers, final int readerFlags) throws ClassNotFoundException, IOException {
        if(!runTransformers) throw new IllegalStateException("ClassNodes must always be provided transformed!");
        MangoLoader loader = (MangoLoader) Thread.currentThread().getContextClassLoader();

        final String canonicalName = name.replace('/', '.');
        final String internalName = name.replace('.', '/');

        final var classBytes = loader.getClassBytes(canonicalName);

        return classNode(canonicalName, internalName, classBytes, readerFlags);
    }

    public @NotNull ClassNode classNode(final @NotNull String canonicalName, final @NotNull String internalName, final byte@NotNull [] input, final int readerFlags) throws ClassNotFoundException {
        if (input.length != 0) {
            final ClassNode node = new ClassNode(Opcodes.ASM9);
            final ClassReader reader = new MixinClassReader(input, canonicalName);
            reader.accept(node, readerFlags);
            return node;
        }
        throw new ClassNotFoundException(canonicalName);
    }

    public static ClassNode getClassNode(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0); // 0 = no special parsing options, because you're basic
        return classNode;
    }


    @Override
    public void registerInvalidClass(String s) {

    }

    @Override
    public boolean isClassLoaded(String name) {
        final var loader = (MangoLoader) Thread.currentThread().getContextClassLoader();

        return loader.hasClass(name);
    }

    @Override
    public String getClassRestrictions(String s) {
        return "";
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        return List.of();
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return List.of();
    }

    @Override
    public void addTransformerExclusion(String s) {

    }
}

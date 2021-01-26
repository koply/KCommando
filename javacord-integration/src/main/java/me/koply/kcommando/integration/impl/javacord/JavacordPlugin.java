package me.koply.kcommando.integration.impl.javacord;

import me.koply.kcommando.plugin.JavaPlugin;
import org.javacord.api.listener.GloballyAttachableListener;

public abstract class JavacordPlugin extends JavaPlugin<GloballyAttachableListener, JavacordCommand> { }
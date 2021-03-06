package com.athaydes.automaton.cli

import com.athaydes.automaton.FXApp
import groovy.transform.CompileStatic

import java.awt.*
import java.lang.instrument.Instrumentation

/**
 * @author Renato
 */

@SuppressWarnings( "GroovyUnusedDeclaration" )
@CompileStatic
class AutomatonJavaAgent {

	static void premain( String agentArgs, Instrumentation instrumentation ) {
		if ( agentArgs ) {
			def toRun = new File( agentArgs )
			if ( toRun.exists() ) {
				waitForWindows {
					if ( Window.windows || FXApp.initialized ) {
						sleep 2_000
						AutomatonScriptRunner.instance.run( toRun.absolutePath, null, true )
					} else {
						println "AutomatonJavaAgent: no Swing or JavaFX window detected"
					}
				}
			} else {
				println "AutomatonJavaAgent: will not start because file '$toRun' does not exist"
			}
		}
	}

	static void waitForWindows( Closure then ) {
		final CYCLE = 1_000 // ms
		final MAX_CYCLES = 30 // cycles

		def cycles = 0

		Thread.start {
			while ( (!Window.windows && !FXApp.initialized) && cycles < MAX_CYCLES ) {
				sleep CYCLE
				cycles++
			}
			then.run()
		}
	}

}

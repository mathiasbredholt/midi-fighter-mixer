package com.djtechtools;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.api.Color;
import java.util.function.Supplier;

class LEDState extends InternalHardwareLightState {
  public int value = 0;
  LEDState(int value) {
    this.value = value;
  }

  @Override
  public boolean equals (final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (this.getClass () != obj.getClass ())
      return false;
    final LEDState other = (LEDState) obj;
    return this.value == other.value;
  }

  @Override
  public HardwareLightVisualState getVisualState () {
    return HardwareLightVisualState.createForColor (Color.whiteColor());
  }
}

class MyKnob {
  public MyKnob(double resetVal, ControllerHost host, MidiIn midiIn, MidiOut midiOut,
                HardwareSurface surface, String name, int cc) {
    this.resetVal = resetVal;

    encoder = surface.createRelativeHardwareKnob(name);
    encoder.setAdjustValueMatcher(
      midiIn.createRelativeBinOffsetCCValueMatcher(0, cc, 127));

    valueBtn = surface.createAbsoluteHardwareKnob(name + ":btn");
    valueBtn.setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(1, cc));

    btn = surface.createHardwareButton(name + ":btnAction");
    btn.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(1, cc, 127));
    // btn.releasedAction().setActionMatcher(midiIn.createCCActionMatcher(1, cc, 0));

    led = surface.createMultiStateHardwareLight(name + ":led");
    led.state().onUpdateHardware((state) -> {
      LEDState ledState = (LEDState) state;
      midiOut.sendMidi(0xb0, cc, ledState.value);
    });

    led.state().setValue(new LEDState((int) resetVal * 127));
  }

  public RelativeHardwareKnob encoder;
  public HardwareButton btn;
  public AbsoluteHardwareKnob valueBtn;
  public MultiStateHardwareLight led;
  public double resetVal = 0.0;
}

public class MIDIFighterMixerExtension extends ControllerExtension {
  private double volumeSensitivity = 0.1;
  private double eqSensivity = 0.1;
  private HardwareSurface surface;

  protected MIDIFighterMixerExtension(final MIDIFighterMixerExtensionDefinition
                                      definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    final ControllerHost host = getHost();
    final MidiIn midiIn = host.getMidiInPort(0);
    final MidiOut midiOut = host.getMidiOutPort(0);

    surface = host.createHardwareSurface();

    final TrackBank bank = host.createMainTrackBank(4, 3, 1);

    final Track track1 = bank.getItemAt(0);
    final Track track2 = bank.getItemAt(1);
    final Track track3 = bank.getItemAt(2);
    final Track track4 = bank.getItemAt(3);

    final MyKnob sendA1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendA", 0);
    final MyKnob sendA2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendA", 1);
    final MyKnob sendA3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendA", 2);
    final MyKnob sendA4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendA", 3);
    final MyKnob sendB1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendB", 4);
    final MyKnob sendB2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendB", 5);
    final MyKnob sendB3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendB", 6);
    final MyKnob sendB4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendB", 7);
    final MyKnob sendC1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendC", 8);
    final MyKnob sendC2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendC", 9);
    final MyKnob sendC3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendC",
                                     10);
    final MyKnob sendC4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendC",
                                     11);

    final MyKnob volume1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: Volume",
                                      12);
    final MyKnob volume2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: Volume",
                                      13);
    final MyKnob volume3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: Volume",
                                      14);
    final MyKnob volume4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: Volume",
                                      15);

    final MyKnob eqHi1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: EQHi", 16);
    final MyKnob eqHi2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: EQHi", 17);
    final MyKnob eqHi3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: EQHi", 18);
    final MyKnob eqHi4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: EQHi", 19);
    final MyKnob eqMid1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: EQMid",
                                     20);
    final MyKnob eqMid2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: EQMid",
                                     21);
    final MyKnob eqMid3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: EQMid",
                                     22);
    final MyKnob eqMid4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: EQMid",
                                     23);
    final MyKnob eqLo1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: EQLo", 24);
    final MyKnob eqLo2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: EQLo", 25);
    final MyKnob eqLo3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: EQLo", 26);
    final MyKnob eqLo4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: EQLo", 27);

    bindVolumeKnob(track1.volume(), volume1, 0);
    bindVolumeKnob(track2.volume(), volume2, 1);
    bindVolumeKnob(track3.volume(), volume3, 2);
    bindVolumeKnob(track4.volume(), volume4, 3);

    final SendBank sendBank1 = track1.sendBank();
    final SendBank sendBank2 = track2.sendBank();
    final SendBank sendBank3 = track3.sendBank();
    final SendBank sendBank4 = track4.sendBank();

    bindKnob(sendBank1.getItemAt(0), sendA1, volumeSensitivity);
    bindKnob(sendBank2.getItemAt(0), sendA2, volumeSensitivity);
    bindKnob(sendBank3.getItemAt(0), sendA3, volumeSensitivity);
    bindKnob(sendBank4.getItemAt(0), sendA4, volumeSensitivity);

    bindKnob(sendBank1.getItemAt(1), sendB1, volumeSensitivity);
    bindKnob(sendBank2.getItemAt(1), sendB2, volumeSensitivity);
    bindKnob(sendBank3.getItemAt(1), sendB3, volumeSensitivity);
    bindKnob(sendBank4.getItemAt(1), sendB4, volumeSensitivity);

    bindKnob(sendBank1.getItemAt(2), sendC1, volumeSensitivity);
    bindKnob(sendBank2.getItemAt(2), sendC2, volumeSensitivity);
    bindKnob(sendBank3.getItemAt(2), sendC3, volumeSensitivity);
    bindKnob(sendBank4.getItemAt(2), sendC4, volumeSensitivity);

    final DeviceMatcher lastInChainMatcher = host.createLastDeviceInChainMatcher();

    final DeviceBank deviceBank1 = track1.createDeviceBank(16);
    final DeviceBank deviceBank2 = track2.createDeviceBank(16);
    final DeviceBank deviceBank3 = track3.createDeviceBank(16);
    final DeviceBank deviceBank4 = track4.createDeviceBank(16);

    deviceBank1.setDeviceMatcher(lastInChainMatcher);
    deviceBank2.setDeviceMatcher(lastInChainMatcher);
    deviceBank3.setDeviceMatcher(lastInChainMatcher);
    deviceBank4.setDeviceMatcher(lastInChainMatcher);

    final RemoteControlsPage eq1 = deviceBank1.getDevice(0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq2 = deviceBank2.getDevice(0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq3 = deviceBank3.getDevice(0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq4 = deviceBank4.getDevice(0).createCursorRemoteControlsPage(3);

    bindKnob(eq1.getParameter(0), eqHi1, eqSensivity);
    bindKnob(eq2.getParameter(0), eqHi2, eqSensivity);
    bindKnob(eq3.getParameter(0), eqHi3, eqSensivity);
    bindKnob(eq4.getParameter(0), eqHi4, eqSensivity);

    bindKnob(eq1.getParameter(1), eqMid1, eqSensivity);
    bindKnob(eq2.getParameter(1), eqMid2, eqSensivity);
    bindKnob(eq3.getParameter(1), eqMid3, eqSensivity);
    bindKnob(eq4.getParameter(1), eqMid4, eqSensivity);

    bindKnob(eq1.getParameter(2), eqLo1, eqSensivity);
    bindKnob(eq2.getParameter(2), eqLo2, eqSensivity);
    bindKnob(eq3.getParameter(2), eqLo3, eqSensivity);
    bindKnob(eq4.getParameter(2), eqLo4, eqSensivity);


    // For now just show a popup notification for verification that it is running.
    host.showPopupNotification("MIDI Fighter Mixer Initialized");
  }

  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
    // For now just show a popup notification for verification that it is no longer running.
    getHost().showPopupNotification("MIDI Fighter Mixer Exited");
  }

  @Override
  public void flush() {
    // TODO Send any updates you need here.
    surface.updateHardware();
  }

  private void changeBank(int bank) {
    getHost().getMidiOutPort(0).sendMidi(0xb3, bank, 127);
  }

  private void bindKnob(Parameter param, MyKnob knob, double sensitivity) {
    knob.encoder.addBindingWithSensitivity(param, sensitivity);
    knob.valueBtn.addBindingWithRange(param, knob.resetVal, knob.resetVal);

    param.value().addValueObserver(128, (value) -> {
      knob.led.state().setValue(new LEDState(value));
    });
  }

  private void bindVolumeKnob(Parameter param, MyKnob knob, int bank) {
    knob.encoder.addBindingWithSensitivity(param, volumeSensitivity);

    // Hardware action = host.createAction(() -> {}, () -> "");
    knob.btn.pressedAction().setBinding(getHost().createAction(() -> { changeBank(bank); }, ()
                                        -> ""));
    // knob.btn.releasedAction().setBinding(getHost().createAction(() -> { changeBank(0); }, ()
    //                                      -> ""));

    param.value().addValueObserver(128, (value) -> {
      knob.led.state().setValue(new LEDState(value));
    });
  }

}

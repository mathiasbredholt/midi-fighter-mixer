package com.djtechtools;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.api.Color;
import java.util.function.Supplier;
import java.util.UUID;

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
  public MyKnob(double resetVal, ControllerHost host, MidiIn midiIn,
                MidiOut midiOut,
                HardwareSurface surface, String name, int cc) {
    this.resetVal = resetVal;
    this.host = host;

    encoder = surface.createRelativeHardwareKnob(name);
    encoder.setAdjustValueMatcher(
      midiIn.createRelativeBinOffsetCCValueMatcher(0, cc, 127));

    valueBtn = surface.createAbsoluteHardwareKnob(name + ":btn");
    valueBtn.setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(1, cc));

    btn = surface.createHardwareButton(name + ":btnAction");
    btn.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(1, cc, 127));

    led = surface.createMultiStateHardwareLight(name + ":led");
    led.state().onUpdateHardware((state) -> {
      LEDState ledState = (LEDState) state;
      midiOut.sendMidi(0xb0, cc, ledState.value);
    });

    led.state().setValue(new LEDState((int) resetVal * 127));
  }

  public void reset() {
    param.value().setImmediately(resetVal);
  }


  public void bind(Parameter param, double sensitivity) {
    this.param = param;
    param.value().markInterested();
    encoder.addBindingWithSensitivity(param, sensitivity);
    valueBtn.addBindingWithRange(param, resetVal, resetVal);

    param.value().addValueObserver(128, (value) -> {
      led.state().setValue(new LEDState(value));
    });
  }

  public void bindVolume(Parameter param, int bank) {
    this.param = param;
    param.value().markInterested();
    encoder.addBindingWithSensitivity(param, volumeSensitivity);
    btn.pressedAction().setBinding(host.createAction(() -> { changeBank(bank); }, ()
                                   -> "changeBank"));
    param.value().addValueObserver(128, (value) -> {
      led.state().setValue(new LEDState(value));
    });
  }

  private void changeBank(int bank) {
    host.getMidiOutPort(0).sendMidi(0xb3, bank, 127);
  }

  public RelativeHardwareKnob encoder;
  public HardwareButton btn;
  public AbsoluteHardwareKnob valueBtn;
  public MultiStateHardwareLight led;
  public double resetVal = 0.0;
  public Parameter param;
  private ControllerHost host;
  final double volumeSensitivity = 0.1;
}

public class MIDIFighterMixerExtension extends ControllerExtension {
  private double eqSensivity = 0.1;
  final double volumeSensitivity = 0.1;
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

    final MidiIn midiIn2 = host.getMidiInPort(1);

    surface = host.createHardwareSurface();

    final TrackBank bank = host.createMainTrackBank(4, 3, 1);

    bank.setSkipDisabledItems(false);
    bank.getItemAt(0).name().markInterested();

    bank.addChannelScrollPositionObserver((int value) -> {
      host.showPopupNotification(bank.getItemAt(0).name().get());
    }, 0);

    final Track track1 = bank.getItemAt(0);
    final Track track2 = bank.getItemAt(1);
    final Track track3 = bank.getItemAt(2);
    final Track track4 = bank.getItemAt(3);

    final MyKnob sendA1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendA",
                                     0);
    final MyKnob sendA2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendA",
                                     1);
    final MyKnob sendA3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendA",
                                     2);
    final MyKnob sendA4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendA",
                                     3);
    final MyKnob sendB1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendB",
                                     4);
    final MyKnob sendB2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendB",
                                     5);
    final MyKnob sendB3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendB",
                                     6);
    final MyKnob sendB4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendB",
                                     7);
    final MyKnob sendC1 = new MyKnob(0, host, midiIn, midiOut, surface, "1: SendC",
                                     8);
    final MyKnob sendC2 = new MyKnob(0, host, midiIn, midiOut, surface, "2: SendC",
                                     9);
    final MyKnob sendC3 = new MyKnob(0, host, midiIn, midiOut, surface, "3: SendC",
                                     10);
    final MyKnob sendC4 = new MyKnob(0, host, midiIn, midiOut, surface, "4: SendC",
                                     11);

    final MyKnob volume1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                      "1: Volume",
                                      12);
    final MyKnob volume2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                      "2: Volume",
                                      13);
    final MyKnob volume3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                      "3: Volume",
                                      14);
    final MyKnob volume4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                      "4: Volume",
                                      15);

    // EQ

    final MyKnob filter1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: Filter",
                                    16);
    final MyKnob filter2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: Filter",
                                    17);
    final MyKnob filter3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: Filter",
                                    18);
    final MyKnob filter4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: Filter",
                                    19);
    final MyKnob eqHi1 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "1: EQHi",
                                     20);
    final MyKnob eqHi2 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "2: EQHi",
                                     21);
    final MyKnob eqHi3 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "3: EQHi",
                                     22);
    final MyKnob eqHi4 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "4: EQHi",
                                     23);
    final MyKnob eqLo1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: EQLo",
                                    24);
    final MyKnob eqLo2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: EQLo",
                                    25);
    final MyKnob eqLo3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: EQLo",
                                    26);
    final MyKnob eqLo4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: EQLo",
                                    27);

    // USER PARAM 1

    final MyKnob userParamA1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserA",
                                          32);
    final MyKnob userParamA2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserA",
                                          33);
    final MyKnob userParamA3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserA",
                                          34);
    final MyKnob userParamA4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserA",
                                          35);
    final MyKnob userParamB1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserB",
                                          36);
    final MyKnob userParamB2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserB",
                                          37);
    final MyKnob userParamB3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserB",
                                          38);
    final MyKnob userParamB4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserB",
                                          39);
    final MyKnob userParamC1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserC",
                                          40);
    final MyKnob userParamC2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserC",
                                          41);
    final MyKnob userParamC3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserC",
                                          42);
    final MyKnob userParamC4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserC",
                                          43);

    // USER PARAM 1

    final MyKnob userParamD1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserD",
                                          48);
    final MyKnob userParamD2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserD",
                                          49);
    final MyKnob userParamD3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserD",
                                          50);
    final MyKnob userParamD4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserD",
                                          51);
    final MyKnob userParamE1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserE",
                                          52);
    final MyKnob userParamE2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserE",
                                          53);
    final MyKnob userParamE3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserE",
                                          54);
    final MyKnob userParamE4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserE",
                                          55);
    final MyKnob userParamF1 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "1: UserF",
                                          56);
    final MyKnob userParamF2 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "2: UserF",
                                          57);
    final MyKnob userParamF3 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "3: UserF",
                                          58);
    final MyKnob userParamF4 = new MyKnob(0, host, midiIn, midiOut, surface,
                                          "4: UserF",
                                          59);

    volume1.bindVolume(track1.volume(), 0);
    volume2.bindVolume(track2.volume(), 1);
    volume3.bindVolume(track3.volume(), 2);
    volume4.bindVolume(track4.volume(), 3);

    final SendBank sendBank1 = track1.sendBank();
    final SendBank sendBank2 = track2.sendBank();
    final SendBank sendBank3 = track3.sendBank();
    final SendBank sendBank4 = track4.sendBank();

    sendA1.bind(sendBank1.getItemAt(0), volumeSensitivity);
    sendA2.bind(sendBank2.getItemAt(0), volumeSensitivity);
    sendA3.bind(sendBank3.getItemAt(0), volumeSensitivity);
    sendA4.bind(sendBank4.getItemAt(0), volumeSensitivity);

    sendB1.bind(sendBank1.getItemAt(1), volumeSensitivity);
    sendB2.bind(sendBank2.getItemAt(1), volumeSensitivity);
    sendB3.bind(sendBank3.getItemAt(1), volumeSensitivity);
    sendB4.bind(sendBank4.getItemAt(1), volumeSensitivity);

    sendC1.bind(sendBank1.getItemAt(2), volumeSensitivity);
    sendC2.bind(sendBank2.getItemAt(2), volumeSensitivity);
    sendC3.bind(sendBank3.getItemAt(2), volumeSensitivity);
    sendC4.bind(sendBank4.getItemAt(2), volumeSensitivity);

    // final DeviceMatcher eqDeviceMatcher = host.createBitwigDeviceMatcher(
    //                                         UUID.fromString ("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb"));
    // final DeviceMatcher lastInChainMatcher = host.createLastDeviceInChainMatcher();
    // final DeviceMatcher lastEqMatcher = host.createAndDeviceMatcher(eqDeviceMatcher,
    //                                     lastInChainMatcher);

    // final DeviceBank deviceBank1 = track1.createDeviceBank(16);
    // final DeviceBank deviceBank2 = track2.createDeviceBank(16);
    // final DeviceBank deviceBank3 = track3.createDeviceBank(16);
    // final DeviceBank deviceBank4 = track4.createDeviceBank(16);

    // deviceBank1.setDeviceMatcher(lastEqMatcher);
    // deviceBank2.setDeviceMatcher(lastEqMatcher);
    // deviceBank3.setDeviceMatcher(lastEqMatcher);
    // deviceBank4.setDeviceMatcher(lastEqMatcher);

    final RemoteControlsPage eq1 = track1.createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq2 = track2.createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq3 = track3.createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq4 = track4.createCursorRemoteControlsPage(3);

    filter1.bind(eq1.getParameter(0), eqSensivity);
    filter2.bind(eq2.getParameter(0), eqSensivity);
    filter3.bind(eq3.getParameter(0), eqSensivity);
    filter4.bind(eq4.getParameter(0), eqSensivity);

    eqHi1.bind(eq1.getParameter(1), eqSensivity);
    eqHi2.bind(eq2.getParameter(1), eqSensivity);
    eqHi3.bind(eq3.getParameter(1), eqSensivity);
    eqHi4.bind(eq4.getParameter(1), eqSensivity);

    eqLo1.bind(eq1.getParameter(2), eqSensivity);
    eqLo2.bind(eq2.getParameter(2), eqSensivity);
    eqLo3.bind(eq3.getParameter(2), eqSensivity);
    eqLo4.bind(eq4.getParameter(2), eqSensivity);

    final DeviceMatcher firstDeviceMatcher = host.createFirstDeviceInChainMatcher();

    final DeviceBank userDeviceBank1 = track1.createDeviceBank(16);
    final DeviceBank userDeviceBank2 = track2.createDeviceBank(16);
    final DeviceBank userDeviceBank3 = track3.createDeviceBank(16);
    final DeviceBank userDeviceBank4 = track4.createDeviceBank(16);

    userDeviceBank1.setDeviceMatcher(firstDeviceMatcher);
    userDeviceBank2.setDeviceMatcher(firstDeviceMatcher);
    userDeviceBank3.setDeviceMatcher(firstDeviceMatcher);
    userDeviceBank4.setDeviceMatcher(firstDeviceMatcher);

    final RemoteControlsPage dev1 = userDeviceBank1.getDevice(
                                      0).createCursorRemoteControlsPage(6);
    final RemoteControlsPage dev2 = userDeviceBank2.getDevice(
                                      0).createCursorRemoteControlsPage(6);
    final RemoteControlsPage dev3 = userDeviceBank3.getDevice(
                                      0).createCursorRemoteControlsPage(6);
    final RemoteControlsPage dev4 = userDeviceBank4.getDevice(
                                      0).createCursorRemoteControlsPage(6);


    userParamA1.bind(dev1.getParameter(0), eqSensivity);
    userParamA2.bind(dev2.getParameter(0), eqSensivity);
    userParamA3.bind(dev3.getParameter(0), eqSensivity);
    userParamA4.bind(dev4.getParameter(0), eqSensivity);

    userParamB1.bind(dev1.getParameter(1), eqSensivity);
    userParamB2.bind(dev2.getParameter(1), eqSensivity);
    userParamB3.bind(dev3.getParameter(1), eqSensivity);
    userParamB4.bind(dev4.getParameter(1), eqSensivity);

    userParamC1.bind(dev1.getParameter(2), eqSensivity);
    userParamC2.bind(dev2.getParameter(2), eqSensivity);
    userParamC3.bind(dev3.getParameter(2), eqSensivity);
    userParamC4.bind(dev4.getParameter(2), eqSensivity);

    userParamD1.bind(dev1.getParameter(3), eqSensivity);
    userParamD2.bind(dev2.getParameter(3), eqSensivity);
    userParamD3.bind(dev3.getParameter(3), eqSensivity);
    userParamD4.bind(dev4.getParameter(3), eqSensivity);

    userParamE1.bind(dev1.getParameter(4), eqSensivity);
    userParamE2.bind(dev2.getParameter(4), eqSensivity);
    userParamE3.bind(dev3.getParameter(4), eqSensivity);
    userParamE4.bind(dev4.getParameter(4), eqSensivity);

    userParamF1.bind(dev1.getParameter(5), eqSensivity);
    userParamF2.bind(dev2.getParameter(5), eqSensivity);
    userParamF3.bind(dev3.getParameter(5), eqSensivity);
    userParamF4.bind(dev4.getParameter(5), eqSensivity);


    HardwareButton resetAllVolumes =
      surface.createHardwareButton("resetAllVolumes");
    resetAllVolumes.pressedAction().setActionMatcher(
      midiIn.createNoteOnActionMatcher(3, 11));
    resetAllVolumes.pressedAction().setBinding(host.createAction(() -> {
      sendA1.reset();
      sendA2.reset();
      sendA3.reset();
      sendA4.reset();
      sendB1.reset();
      sendB2.reset();
      sendB3.reset();
      sendB4.reset();
      sendC1.reset();
      sendC2.reset();
      sendC3.reset();
      sendC4.reset();
      volume1.reset();
      volume2.reset();
      volume3.reset();
      volume4.reset();
      filter1.reset();
      filter2.reset();
      filter3.reset();
      filter4.reset();
      eqHi1.reset();
      eqHi2.reset();
      eqHi3.reset();
      eqHi4.reset();
      eqLo1.reset();
      eqLo2.reset();
      eqLo3.reset();
      eqLo4.reset();
      userParamA1.reset();
      userParamA2.reset();
      userParamA3.reset();
      userParamA4.reset();
      userParamB1.reset();
      userParamB2.reset();
      userParamB3.reset();
      userParamB4.reset();
      userParamC1.reset();
      userParamC2.reset();
      userParamC3.reset();
      userParamC4.reset();
      userParamD1.reset();
      userParamD2.reset();
      userParamD3.reset();
      userParamD4.reset();
      userParamE1.reset();
      userParamE2.reset();
      userParamE3.reset();
      userParamE4.reset();
      userParamF1.reset();
      userParamF2.reset();
      userParamF3.reset();
      userParamF4.reset();
    }, () -> "resetAllVolumes"));

    HardwareButton nextBank = surface.createHardwareButton("nextBank");
    HardwareButton previousBank = surface.createHardwareButton("previousBank");

    // nextBank.pressedAction().setActionMatcher(
    //   midiIn.createNoteOnActionMatcher(3, 13));
    // previousBank.pressedAction().setActionMatcher(
    //   midiIn.createNoteOnActionMatcher(3, 10));

    nextBank.pressedAction().setActionMatcher(
      midiIn2.createCCActionMatcher(0, 107, 127));
    previousBank.pressedAction().setActionMatcher(
      midiIn2.createCCActionMatcher(0, 106, 127));

    nextBank.pressedAction().setBinding(host.createAction(() -> {
      bank.scrollPageForwards();
    }, () -> "nextBank"));

    previousBank.pressedAction().setBinding(host.createAction(() -> {
      bank.scrollPageBackwards();
    }, () -> "previousBank"));

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
}

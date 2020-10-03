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
    TrackBank allTracks = host.createMainTrackBank(64, 3, 1);

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

    final MyKnob eqHi1 = new MyKnob(0.5, host, midiIn, midiOut, surface, "1: EQHi",
                                    16);
    final MyKnob eqHi2 = new MyKnob(0.5, host, midiIn, midiOut, surface, "2: EQHi",
                                    17);
    final MyKnob eqHi3 = new MyKnob(0.5, host, midiIn, midiOut, surface, "3: EQHi",
                                    18);
    final MyKnob eqHi4 = new MyKnob(0.5, host, midiIn, midiOut, surface, "4: EQHi",
                                    19);
    final MyKnob eqMid1 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "1: EQMid",
                                     20);
    final MyKnob eqMid2 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "2: EQMid",
                                     21);
    final MyKnob eqMid3 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "3: EQMid",
                                     22);
    final MyKnob eqMid4 = new MyKnob(0.5, host, midiIn, midiOut, surface,
                                     "4: EQMid",
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

    final DeviceMatcher eqDeviceMatcher = host.createBitwigDeviceMatcher(
                                            UUID.fromString ("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb"));
    final DeviceMatcher lastInChainMatcher = host.createLastDeviceInChainMatcher();
    final DeviceMatcher lastEqMatcher = host.createAndDeviceMatcher(eqDeviceMatcher,
                                        lastInChainMatcher);

    final DeviceBank deviceBank1 = track1.createDeviceBank(16);
    final DeviceBank deviceBank2 = track2.createDeviceBank(16);
    final DeviceBank deviceBank3 = track3.createDeviceBank(16);
    final DeviceBank deviceBank4 = track4.createDeviceBank(16);

    deviceBank1.setDeviceMatcher(lastEqMatcher);
    deviceBank2.setDeviceMatcher(lastEqMatcher);
    deviceBank3.setDeviceMatcher(lastEqMatcher);
    deviceBank4.setDeviceMatcher(lastEqMatcher);

    final RemoteControlsPage eq1 = deviceBank1.getDevice(
                                     0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq2 = deviceBank2.getDevice(
                                     0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq3 = deviceBank3.getDevice(
                                     0).createCursorRemoteControlsPage(3);
    final RemoteControlsPage eq4 = deviceBank4.getDevice(
                                     0).createCursorRemoteControlsPage(3);

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


    bindKnob(dev1.getParameter(0), userParamA1, eqSensivity);
    bindKnob(dev2.getParameter(0), userParamA2, eqSensivity);
    bindKnob(dev3.getParameter(0), userParamA3, eqSensivity);
    bindKnob(dev4.getParameter(0), userParamA4, eqSensivity);

    bindKnob(dev1.getParameter(1), userParamB1, eqSensivity);
    bindKnob(dev2.getParameter(1), userParamB2, eqSensivity);
    bindKnob(dev3.getParameter(1), userParamB3, eqSensivity);
    bindKnob(dev4.getParameter(1), userParamB4, eqSensivity);

    bindKnob(dev1.getParameter(2), userParamC1, eqSensivity);
    bindKnob(dev2.getParameter(2), userParamC2, eqSensivity);
    bindKnob(dev3.getParameter(2), userParamC3, eqSensivity);
    bindKnob(dev4.getParameter(2), userParamC4, eqSensivity);

    bindKnob(dev1.getParameter(3), userParamD1, eqSensivity);
    bindKnob(dev2.getParameter(3), userParamD2, eqSensivity);
    bindKnob(dev3.getParameter(3), userParamD3, eqSensivity);
    bindKnob(dev4.getParameter(3), userParamD4, eqSensivity);

    bindKnob(dev1.getParameter(4), userParamE1, eqSensivity);
    bindKnob(dev2.getParameter(4), userParamE2, eqSensivity);
    bindKnob(dev3.getParameter(4), userParamE3, eqSensivity);
    bindKnob(dev4.getParameter(4), userParamE4, eqSensivity);

    bindKnob(dev1.getParameter(5), userParamF1, eqSensivity);
    bindKnob(dev2.getParameter(5), userParamF2, eqSensivity);
    bindKnob(dev3.getParameter(5), userParamF3, eqSensivity);
    bindKnob(dev4.getParameter(5), userParamF4, eqSensivity);


    HardwareButton resetAllVolumes =
      surface.createHardwareButton("resetAllVolumes");
    resetAllVolumes.pressedAction().setActionMatcher(
      midiIn.createNoteOnActionMatcher(3, 11));
    resetAllVolumes.pressedAction().setBinding(host.createAction(() -> {
      for (int i = 0; i < allTracks.getSizeOfBank(); ++i) {
        allTracks.getItemAt(i).volume().setImmediately(0.0);
        allTracks.getItemAt(i).sendBank().getItemAt(0).setImmediately(0.0);
        allTracks.getItemAt(i).sendBank().getItemAt(1).setImmediately(0.0);
        allTracks.getItemAt(i).sendBank().getItemAt(2).setImmediately(0.0);
      }
    }, () -> "resetAllVolumes"));

    HardwareButton nextBank = surface.createHardwareButton("nextBank");
    HardwareButton previousBank = surface.createHardwareButton("previousBank");

    nextBank.pressedAction().setActionMatcher(
      midiIn.createNoteOnActionMatcher(3, 13));
    previousBank.pressedAction().setActionMatcher(
      midiIn.createNoteOnActionMatcher(3, 10));

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
    knob.btn.pressedAction().setBinding(getHost().createAction(() -> { changeBank(bank); }, ()
                                        -> "changeBank"));
    param.value().addValueObserver(128, (value) -> {
      knob.led.state().setValue(new LEDState(value));
    });
  }

}

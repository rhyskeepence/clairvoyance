package clairvoyance.examples

import org.specs2.clairvoyance.{ClairvoyantContext, ProducesCapturedInputsAndOutputs, ClairvoyantSpec}
import org.specs2.matcher.{MatchResult, Matcher}
import java.text.SimpleDateFormat
import java.util.Date

class GivenWhenThenExample extends ClairvoyantSpec {

  "The coordinator" should {
    "invoke the Doomsday Device on the 21st of December 2012" in new context {
      "Given the date is ${21/12/2012}"               ===> givenTheDateIs
      "When the coordinator runs"                     ===> theCoordinatorRuns
      "Then the Doomsday device should be unleashed"  ===> doomsdayDeviceIsUnleashed
    }
  }

  trait context extends ClairvoyantContext {
    val theDoomsdayDevice = new StubDoomsdayDevice
    val clock = new StubClock

    def toDate(ddMMyyyy: String) = new SimpleDateFormat("dd/MM/yyyy").parse(ddMMyyyy)

    override def capturedInputsAndOutputs = Seq(theDoomsdayDevice)

    def beUnleashed: Matcher[StubDoomsdayDevice] = (d: StubDoomsdayDevice) =>
      (d.wasUnleashed, d + " was unleashed", d + " was not unleashed")

    def givenTheDateIs: (String) => Unit = {
      (s: String) =>
        clock.setDateTo(toDate(s))
    }

    def theCoordinatorRuns(): Unit = {
      new MasterCoordinator(theDoomsdayDevice, clock).runIt()
    }

    def doomsdayDeviceIsUnleashed: MatchResult[StubDoomsdayDevice] = {
      theDoomsdayDevice should beUnleashed
    }
  }
}

class StubDoomsdayDevice extends ProducesCapturedInputsAndOutputs {
  var unleashed = false

  def unleash(date: Date): Unit = { unleashed = true }

  def wasUnleashed = unleashed
}

class StubClock extends ProducesCapturedInputsAndOutputs {
  var date: Date = new Date

  def setDateTo(date: Date): Unit = { this.date = date }
}

class MasterCoordinator(device: StubDoomsdayDevice, clock: StubClock) {
  def runIt(): Unit = { device.unleash(clock.date) }
}

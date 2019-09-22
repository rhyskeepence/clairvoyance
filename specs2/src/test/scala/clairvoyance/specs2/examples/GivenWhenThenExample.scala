package clairvoyance.specs2.examples

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.specs2.{ClairvoyantSpec, ClairvoyantContext}
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.matcher.{MatchResult, Matcher}

class GivenWhenThenExample extends ClairvoyantSpec {

  "The coordinator" should {
    "not invoke the Doomsday Device on the 20th of December 2012" in new context {
      "Given the date is 20/12/2012" ===> givenTheDateIs("20/12/2012")
      "When the coordinator runs" ===> theCoordinatorRuns
      "Then the Doomsday device should not be unleashed" ===> doomsdayDeviceIsNotUnleashed
    }

    "invoke the Doomsday Device on the 21st of December 2012" in new context {
      "Given the date is 21/12/2012" ===> givenTheDateIs("21/12/2012")
      "When the coordinator runs" ===> theCoordinatorRuns
      "Then the Doomsday device should be unleashed" ===> doomsdayDeviceIsUnleashed
    }
  }

  trait context extends ClairvoyantContext {
    val theDoomsdayDevice = new StubDoomsdayDevice
    val clock             = new StubClock

    override def capturedInputsAndOutputs = Seq(theDoomsdayDevice)

    def beUnleashed: Matcher[StubDoomsdayDevice] =
      (d: StubDoomsdayDevice) => (d.wasUnleashed, d + " was unleashed", d + " was not unleashed")

    def givenTheDateIs(date: String): Unit =
      clock.setDateTo(new SimpleDateFormat("dd/MM/yyyy").parse(date))

    def theCoordinatorRuns(): Unit = {
      new MasterCoordinator(theDoomsdayDevice, clock).runIt()
    }

    def doomsdayDeviceIsNotUnleashed: MatchResult[StubDoomsdayDevice] = {
      theDoomsdayDevice should beUnleashed.not
    }

    def doomsdayDeviceIsUnleashed: MatchResult[StubDoomsdayDevice] = {
      theDoomsdayDevice should beUnleashed
    }
  }
}

class StubDoomsdayDevice extends ProducesCapturedInputsAndOutputs {
  val formatter = new SimpleDateFormat("dd/MM/yyyy")
  var unleashed = false

  def unleash(date: Date): Unit = { unleashed = formatter.format(date).equals("21/12/2012") }

  def wasUnleashed = unleashed
}

class StubClock extends ProducesCapturedInputsAndOutputs {
  var date: Date = new Date

  def setDateTo(date: Date): Unit = { this.date = date }
}

class MasterCoordinator(device: StubDoomsdayDevice, clock: StubClock) {
  def runIt(): Unit = { device.unleash(clock.date) }
}

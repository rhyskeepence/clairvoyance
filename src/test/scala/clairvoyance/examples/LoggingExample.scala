package clairvoyance.examples

import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, ClairvoyantSpec}
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.matcher.{Matcher, Expectable}

class LoggingExample extends ClairvoyantSpec {

  "The co-ordinator" should {
    "invoke the Doomsday Device on the 21st of December 2012" in new context {
      givenTheDateIs("21/12/2012")
      whenTheCoordinatorRuns()
      theDoomsdayDevice should beUnleashed
    }
  }

  trait context extends ClairvoyantContext {
    val theDoomsdayDevice = new StubDoomsdayDevice
    val clock = new StubClock

    override def capturedInputsAndOutputs = Seq(theDoomsdayDevice)

    def givenTheDateIs(ddMMyyyy: String) {
      clock.setDateTo(toDate(ddMMyyyy))
    }

    def whenTheCoordinatorRuns() {
      val coordinator = new MasterCoordinator(theDoomsdayDevice, clock)
      coordinator.runIt()
    }

    def beUnleashed: Matcher[StubDoomsdayDevice] = (d: StubDoomsdayDevice) =>
      (d.wasUnleashed, d + " was unleashed", d + " was not unleashed")


    class StubClock extends Clock {
      def setDateTo(time: Date) {
        interestingGivens += ("Current date" -> time)
      }

      def currentTime = {
        interestingGivens("Current date")
          .map(_.asInstanceOf[Date])
          .getOrElse(sys.error("No date set"))
      }
    }


    class StubDoomsdayDevice extends DoomsdayDevice with ProducesCapturedInputsAndOutputs {
      var wasUnleashed = false

      def unleashDestruction(target: String) {
        wasUnleashed = true

        interestingGivens += ("Target" -> target)

        val output =
          <unleashDestruction>
            <target>{target}</target>
          </unleashDestruction>

        captureValue("Doomsday Device Output" -> output)
      }
    }
  }

  /* Below is the system under test */
  class MasterCoordinator(doomsdayDevice: DoomsdayDevice, clock: Clock) {
    val triggerDate = toDate("21/12/2012")
    
    def runIt() {
      if (clock.currentTime equals triggerDate)
        doomsdayDevice.unleashDestruction("Planet Earth")
    }
  }

  trait Clock {
    def currentTime: Date
  }

  trait DoomsdayDevice {
    def unleashDestruction(target: String)
  }

  def toDate(ddMMyyyy: String) = new SimpleDateFormat("dd/MM/yyyy").parse(ddMMyyyy)
}
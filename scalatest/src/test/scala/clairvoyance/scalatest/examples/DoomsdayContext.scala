package clairvoyance.scalatest.examples

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.scalatest.ClairvoyantContext
import java.text.SimpleDateFormat
import java.util.Date
import org.scalatest.{Matchers, OneInstancePerTest, Suite}

trait DoomsdayContext extends ClairvoyantContext with OneInstancePerTest with Matchers { this: Suite =>
  val theDoomsdayDevice = new StubDoomsdayDevice
  val clock = new StubClock

  override def capturedInputsAndOutputs = Seq(theDoomsdayDevice)

  def setDateTo(ddMMyyyy: String): Unit = clock.setDateTo(toDate(ddMMyyyy))
  private def toDate(ddMMyyyy: String) = new SimpleDateFormat("dd/MM/yyyy").parse(ddMMyyyy)

  def runTheCoordinator(): Unit = new MasterCoordinator(theDoomsdayDevice, clock).runIt()

  def unleashed = 'wasUnleashed

  /* Below is the system under test */
  class MasterCoordinator(doomsdayDevice: DoomsdayDevice, clock: Clock) {
    val triggerDate = toDate("21/12/2012")

    def runIt(): Unit = {
      if (clock.currentTime equals triggerDate)
        doomsdayDevice.unleashDestruction("Planet Earth")
    }
  }

  trait DoomsdayDevice {
    def unleashDestruction(target: String): Unit
  }

  trait Clock {
    def currentTime: Date
  }

  class StubClock extends Clock {
    def setDateTo(time: Date): Unit = interestingGivens += ("Current date" -> time)

    def currentTime =
      interestingGivens("Current date")
        .map(_.asInstanceOf[Date])
        .getOrElse(sys.error("No date set"))
  }

  class StubDoomsdayDevice extends DoomsdayDevice with ProducesCapturedInputsAndOutputs {
    var wasUnleashed = false

    def unleashDestruction(target: String): Unit = {
      wasUnleashed = true
      interestingGivens += ("Target" -> target)

      captureValue("Doomsday Device Output" ->
        <unleashDestruction>
          <target>{target}</target>
        </unleashDestruction>
      )
    }
  }
}

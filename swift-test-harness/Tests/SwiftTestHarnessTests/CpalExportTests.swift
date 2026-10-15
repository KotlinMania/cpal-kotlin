#if canImport(Testing)
import Testing
import Cpal

@Suite("Cpal Swift Export Tests")
struct CpalExportTests {
    @Test("Cpal swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "Cpal swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import Cpal

final class CpalExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "Cpal swift module imported cleanly")
    }
}
#endif

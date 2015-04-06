Feature: proxy
    In order to maintain high service availability during individual service outage
    As a service owner
    I want to load balance requests between the available services
    
Scenario: Proxy single service
    Given a server "http://localhost/test/service"
    And "http://localhost/test/service" is proxied at "http://localhost/proxy/test/service"
    When a request is made to "http://localhost/proxy/test/service"
    Then a successful response will be received
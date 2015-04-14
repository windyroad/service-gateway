Feature: proxy
    In order to maintain high service availability during individual service outage
    As a service owner
    I want to load balance requests between the available services
    
Scenario: Proxy single service
    Given there are no proxied endpoints listed
    And a ping service at "/test/ping"
    And "/test" is proxied at "/proxy/test"
    When a request is successfully made to "/proxy/test/ping"
    Then "/test/ping" will be listed in the endpoints proxied by "/proxy/test"
    And the endpoint will be shown as available
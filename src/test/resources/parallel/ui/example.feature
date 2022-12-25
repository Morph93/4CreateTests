@UI @Login
Feature: Login scenarios

  Background:
    Given User is redirected to the login page

  @Smoke @Regression
  Scenario: Check all elements of the login page
    Then All elements should be present on loginPage page


  Scenario Outline: User tries to login with set of data
    When  User inputs "<setOfData>" set of data in username and password
    And   User press Login button for "<setOfData>" set of data
    Then  User should be presented with "<setOfData>" result

    @Smoke
    Examples:
      | setOfData |
      | admin     |
      | client    |
      | empty     |

    @Regression
    Examples:
      | setOfData                       |
      | invalidEmail                    |
      | invalidPassword                 |
      | invalidEmailWithoutMonkey       |
      | invalidPasswordOver30Characters |

